package protocol.command;

import cache.Cache;
import cache.CacheEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public class MockCache implements Cache {
  private final Map<String, CacheEntry> map;
  private final Supplier<CacheEntry> getFaultInjector;
  private final Supplier<Boolean> setFaultInjector;

  public MockCache() {
    this(null, null);
  }

  public MockCache(final Supplier<CacheEntry> getFaultInjector, final Supplier<Boolean> setFaultInjector) {
    this.getFaultInjector = getFaultInjector;
    this.setFaultInjector = setFaultInjector;
    map = new HashMap<>();
  }

  @Override
  public boolean contains(@Nonnull CacheEntry entry) {
    return false;
  }

  @Override
  public CacheEntry get(@Nonnull String key) {
    if (getFaultInjector != null) {
      return getFaultInjector.get();
    }

    return map.get(key);
  }

  @Override
  public boolean set(@Nonnull CacheEntry entry) {
    if (setFaultInjector != null) {
      return setFaultInjector.get();
    }

    map.put(entry.getKey(), entry);
    return true;
  }

  @Override
  public boolean add(@Nonnull CacheEntry entry) {
    if (setFaultInjector != null) {
      return setFaultInjector.get();
    }

    return set(entry);
  }

  @Override
  public boolean replace(@Nonnull CacheEntry entry) {
    if (setFaultInjector != null) {
      return setFaultInjector.get();
    }

    return set(entry);
  }

  @Override
  public boolean delete(@Nonnull CacheEntry entry) {
    return false;
  }
}
