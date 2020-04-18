package cache;

import static cache.EvictionPolicyListener.*;

import cache.EvictionPolicyListener.Operation;
import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;

public final class MemCached implements Cache {
  private final Map<String, LinkedCacheEntry> cache;
  private final EvictionPolicyListener policyListener;

  public MemCached(final int maxCacheSize, @Nonnull final EvictionPolicyListener policyListener) {
    this.policyListener = policyListener;
    // Using Concurrent HasMap instead of HashTable or Synchronized HashMap.
    // A concurrent collection is thread-safe, but not governed by a single exclusion lock. In the particular case of ConcurrentHashMap, it safely permits any number of concurrent reads as well
    // as a tunable number of concurrent writes. "Synchronized" classes can be useful when you need to prevent all access to a collection via a single lock, at the expense of poorer scalability.
    // In other cases in which multiple threads are expected to access a common collection, "concurrent" versions are normally preferable.
    // More Details: https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/package-summary.html#MemoryVisibility
    // Also Chaining is handled by either using LinkedList (worst case O(n) or Balanced tree (worst case O(log n).
    this.cache = new ConcurrentHashMap<>(maxCacheSize);
  }

  @Override
  public CacheEntry get(@Nonnull final String key) {
    Preconditions.checkArgument(key != null && !key.isEmpty(), "key can not be null or empty");

    if (!cache.containsKey(key)) {
      CacheStats.getInstance().reportCacheMiss();
      return null;
    }

    final LinkedCacheEntry linkedCacheEntry = cache.get(key);
    policyListener.notify(new Message(this, linkedCacheEntry, Operation.GET));
    CacheStats.getInstance().reportCacheHit();

    return linkedCacheEntry.getEntry();
  }

  @Override
  public boolean set(@Nonnull final CacheEntry entry) {
    Preconditions.checkArgument(entry != null, "entry can not be null");

    if (contains(entry)) {
      return replace(entry);
    }

    final LinkedCacheEntry linkedCacheEntry = new LinkedCacheEntry(entry, null, null);
    cache.put(entry.getKey(), linkedCacheEntry);
    policyListener.notify(new Message(this, linkedCacheEntry, Operation.PUT));

    return true;
  }

  @Override
  public boolean contains(@Nonnull final CacheEntry entry) {
    Preconditions.checkArgument(entry != null,"entry can not be null");

    return cache.containsKey(entry.getKey());
  }

  @Override
  public boolean add(@Nonnull final CacheEntry entry) {
    Preconditions.checkArgument(entry != null, "entry can not be null");

    if(contains(entry)) {
      return false;
    }

    set(entry);

    return true;
  }

  @Override
  public boolean replace(@Nonnull CacheEntry entry) {
    Preconditions.checkArgument(entry != null, "entry can not be null");

    if(!contains(entry)) {
      return false;
    }

    final LinkedCacheEntry linkedCacheEntry = cache.get(entry.getKey());
    linkedCacheEntry.setEntry(entry);
    cache.put(entry.getKey(), linkedCacheEntry);
    policyListener.notify(new Message(this, linkedCacheEntry, Operation.GET));

    return true;
  }

  @Override
  public boolean delete(@Nonnull final CacheEntry entry) {
    Preconditions.checkArgument(entry != null, "entry can not be null");

    cache.remove(entry.getKey());

    return true;
  }
}
