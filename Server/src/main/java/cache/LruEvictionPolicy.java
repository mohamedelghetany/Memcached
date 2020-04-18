package cache;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import javax.annotation.Nonnull;
import org.apache.log4j.Logger;

public final class LruEvictionPolicy implements EvictionPolicyListener {
  private static final Logger logger = Logger.getLogger(LruEvictionPolicy.class);

  private final BlockingDeque<Message> queue;
  private final LruLinkedList lruList;
  private final int maxCacheSize;
  private int count;

  public LruEvictionPolicy(final int maxCacheSize) {
    this(new LinkedBlockingDeque<>(), maxCacheSize);
  }

  @VisibleForTesting
  public LruEvictionPolicy(final @Nonnull BlockingDeque<Message> queue, final int maxCacheSize) {
    this.queue = queue;
    this.maxCacheSize = maxCacheSize;
    lruList = new LruLinkedList();

    final Thread thread = new Thread(new LruWorker());
    thread.setName("LRU-Worker thread");
    thread.start();
  }

  @Override
  public void notify(@Nonnull final Message message) {
    queue.offer(message);
  }

  private class LruWorker implements Runnable {

    @Override
    public void run() {
      logger.info("Starting LruWorker thread....");

      while (true) {
        try {
          final Message message = queue.take();
          logger.info("Deque message " + message.getEntry());

          lruList.moveToFirst(message.getEntry());

          if (Operation.PUT.equals(message.getOperation())) {
            count++;
          }

          while (count > maxCacheSize) {
            final LinkedCacheEntry linkedCacheEntry = lruList.removeLast();
            message.getCache().delete(linkedCacheEntry.getEntry());
            count--;
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
}
