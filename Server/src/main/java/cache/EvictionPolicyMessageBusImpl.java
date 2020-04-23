package cache;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import javax.annotation.Nonnull;

/**
 * Implementation of {@link EvictionPolicyMessageBus}
 *
 * Internally it uses {@link BlockingDeque} so the consumer thread ({@link EvictionPolicyLRUWorker}) will block and wait when the queue is empty
 */
public final class EvictionPolicyMessageBusImpl implements EvictionPolicyMessageBus {
  private final BlockingDeque<Message> queue;

  public EvictionPolicyMessageBusImpl(final int maxCacheSize) {
    this(new LinkedBlockingDeque<>(), maxCacheSize);
  }

  @VisibleForTesting
  public EvictionPolicyMessageBusImpl(final @Nonnull BlockingDeque<Message> queue, final int maxCacheSize) {
    this.queue = queue;
  }

  @Override
  public void publish(@Nonnull final Message message) {
    queue.offer(message);
  }

  @Override
  public Message fetch() throws InterruptedException {
    return queue.take();
  }
}
