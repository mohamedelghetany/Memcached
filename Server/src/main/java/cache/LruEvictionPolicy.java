package cache;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import javax.annotation.Nonnull;
import org.apache.log4j.Logger;

public final class LruEvictionPolicy implements EvictionPolicyListener {
  private static final Logger logger = Logger.getLogger(LruEvictionPolicy.class);

  private final BlockingDeque<EvictionPolicyMessage> queue;
  private final LruLinkedList lruList;
  private final int maxCacheSize;
  private int count;

  public LruEvictionPolicy(final int maxCacheSize) {
    this.maxCacheSize = maxCacheSize;
    this.queue = new LinkedBlockingDeque<>();
    lruList = new LruLinkedList();

    final Thread thread = new Thread(new LruWorker());
    thread.setName("LRU-Worker thread");
    thread.start();
  }

  @Override
  public void accept(@Nonnull final EvictionPolicyMessage message) {
    queue.offer(message);
  }

  private class LruWorker implements Runnable {

    @Override
    public void run() {
      logger.info("Starting LruWorker thread....");

      while (true) {
        try {
          final EvictionPolicyMessage message = queue.take();
          logger.debug("Deque message " + message.getEntry());

          lruList.moveToFirst(message.getEntry());
          count++;

          while (count > maxCacheSize) {
            final LinkedCacheEntry linkedCacheEntry = lruList.removeLast();
            message.getCache().remove(linkedCacheEntry);
            count--;

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
