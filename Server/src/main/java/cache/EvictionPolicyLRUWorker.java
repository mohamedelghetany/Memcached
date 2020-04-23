package cache;

import static cache.EvictionPolicyMessageBus.Operation.ADD;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.apache.log4j.Logger;

/**
 * An LRU single worker thread that will consume from {@link EvictionPolicyMessageBus} and apply the necessary LRU changes on the
 * underlying LinkedList {@link LruLinkedList}
 */
public class EvictionPolicyLRUWorker implements Runnable {
  private static Logger logger = Logger.getLogger(EvictionPolicyLRUWorker.class);

  private final LruLinkedList lruList;
  private final int maxCacheSize;
  private int currentCacheSize;
  private final EvictionPolicyMessageBus messageBus;

  @VisibleForTesting
  public EvictionPolicyLRUWorker(@Nonnull final EvictionPolicyMessageBus messageBus, final int maxCacheSize) {
    Preconditions.checkArgument(messageBus != null, "messageBus can not be null");
    Preconditions.checkArgument(maxCacheSize >= 0, "maxCacheSize can not be < 0");

    this.messageBus = messageBus;
    this.maxCacheSize = maxCacheSize;
    lruList = new LruLinkedList();
  }

  @Override
  public void run() {
    logger.info("Starting EvictionPolicyLRUWorker thread....");
    run(() -> true);
  }

  /**
   * Keep running in a loop while the given condition is true!
   *
   * @param condition the condition when meet Run will end (the thread will stop)
   */
  public void run(@Nonnull final Supplier<Boolean> condition) {
    while (condition.get()) {
      try {
        final EvictionPolicyMessageBus.Message message = messageBus.fetch();
        logger.debug("Deque message " + message.getEntry());

        lruList.moveToFirst(message.getEntry());

        if (ADD.equals(message.getOperation())) {
          currentCacheSize++;
        }

        while (currentCacheSize > maxCacheSize) {
          final LinkedCacheEntry linkedCacheEntry = lruList.removeLast();
          message.getCache().delete(linkedCacheEntry.getEntry());
          currentCacheSize--;
          CacheStats.getInstance().reportEviction();
          logger.debug("Evicted " + linkedCacheEntry.toString());
        }

      } catch (final Exception e) {
        // We want this thread to keep running so we don't wanna any exception to escape
        // In here we will just log, yes we might have failed to update the LRU for this
        // item but we will depend on it will be updated next time it is called.
        logger.error("Error while pooling from the queue", e);
      }
    }
  }
}
