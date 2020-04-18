package cache;

import javax.annotation.Nonnull;

public interface EvictionPolicyListener {

  void notify(@Nonnull final Message message);

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

  enum  Operation {
    GET,
    PUT
  }
}
