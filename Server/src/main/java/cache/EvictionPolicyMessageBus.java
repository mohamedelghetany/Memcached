package cache;

import javax.annotation.Nonnull;

public interface EvictionPolicyMessageBus {

  void publish(@Nonnull final Message message);

  Message fetch() throws InterruptedException;

  /**
   * The notification message that used when notifying Eviction policy about
   * the updates in the cache.
   *
   * Something to note: The message takes an instance of {@link Cache} which the value
   * has been added/modified at. The main reason behind passign this cache is providing the ability
   * of scaling this design. We can partition the cache (Have multiple caches) that uses
   * one eviction policy
   */
  class Message {
    private final Cache cache;
    private final LinkedCacheEntry entry;
    private final Operation operation;

    public Message(@Nonnull final Cache cache, @Nonnull final LinkedCacheEntry entry, @Nonnull final Operation operation) {
      this.cache = cache;
      this.entry = entry;
      this.operation = operation;
    }

    public LinkedCacheEntry getEntry() {
      return entry;
    }

    public Cache getCache() {
      return cache;
    }

    public Operation getOperation() {
      return operation;
    }
  }

  /**
   * A simplified enum indicates what operation happened on the cache.
   * This enum is different than the supported commands. The mapping is not 1:1
   * Multiple command can map to one operation here.
   */
  enum  Operation {
    MODIFY,
    ADD,
    GET
  }
}
