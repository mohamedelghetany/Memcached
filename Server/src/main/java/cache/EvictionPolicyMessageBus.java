package cache;

import javax.annotation.Nonnull;

/**
 * Interface of Eviction Policy MessageBus. This interface exists because we want to decouple Updating the cache from Eviction process
 *
 * Order of the messages should be guaranteed, a message1 that is added to the bus before message2 is guaranteed to get consumed first
 *
 * The producer for this Bus is {@link Cache}
 * The consumer for this Bus is Eviction Worker
 */
public interface EvictionPolicyMessageBus {

  /**
   * Publish message to the Bus
   *
   * @param message to be published
   */
  void publish(@Nonnull final Message message);

  /**
   * Fetch the message form the Bus
   *
   * Fetch might Block and wait for a message, depends on the concrete implementation
   *
   * @throws InterruptedException
   */
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
