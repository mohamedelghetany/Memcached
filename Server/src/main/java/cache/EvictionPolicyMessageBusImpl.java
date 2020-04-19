package cache;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import javax.annotation.Nonnull;

public final class EvictionPolicyMessageBusImpl implements EvictionPolicyMessageBus {
  private final BlockingDeque<Message> queue;

  public EvictionPolicyMessageBusImpl(final int maxCacheSize) {
    this(new LinkedBlockingDeque<>(), maxCacheSize);
  }

  @VisibleForTesting
  public EvictionPolicyMessageBusImpl(final @Nonnull BlockingDeque<Message> queue, final int maxCacheSize) {
    this.queue = queue;

    final Thread thread = new Thread(new EvictionPolicyLRUWorker(queue, maxCacheSize));
    thread.setName("LRU-Worker thread");
    thread.start();
  }

  @Override
  public void publish(@Nonnull final Message message) {
    queue.offer(message);
  }
}
