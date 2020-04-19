package cache;

import static cache.EvictionPolicyMessageBus.Operation.ADD;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.BlockingDeque;
import javax.annotation.Nonnull;
import org.apache.log4j.Logger;

public class EvictionPolicyLRUWorker implements Runnable {
  private static Logger logger = Logger.getLogger(EvictionPolicyLRUWorker.class);

  private final LruLinkedList lruList;
  private final int maxCacheSize;
  private final BlockingDeque<EvictionPolicyMessageBus.Message> queue;
  private int currentCacheSize;

  @VisibleForTesting
  public EvictionPolicyLRUWorker(final @Nonnull BlockingDeque<EvictionPolicyMessageBus.Message> queue, final int maxCacheSize) {
    this.queue = queue;
    this.maxCacheSize = maxCacheSize;
    lruList = new LruLinkedList();
  }

  @Override
  public void run() {
    logger.info("Starting EvictionPolicyLRUWorker thread....");

    while (true) {
      try {
        final EvictionPolicyMessageBus.Message message = queue.take();
        logger.info("Deque message " + message.getEntry());

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
