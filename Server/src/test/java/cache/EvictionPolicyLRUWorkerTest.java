package cache;

import java.util.concurrent.LinkedBlockingDeque;
import org.junit.Assert;
import org.junit.Test;

public class EvictionPolicyLRUWorkerTest {

  @Test (expected = IllegalArgumentException.class)
  public void testFailsT0CreateWorkerIfSizeLessThan0 () {
    final LinkedBlockingDeque<EvictionPolicyMessageBus.Message> blockingQueue = new LinkedBlockingDeque<>();
    new EvictionPolicyLRUWorker(new EvictionPolicyMessageBusImpl(blockingQueue, 1), -1);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testFailsToCreateWorkerIfMessageBusIsNull () {
    new EvictionPolicyLRUWorker(null, 10);
  }

  @Test
  public void testRunLinksValueInLRUFashion() {
    final LinkedBlockingDeque<EvictionPolicyMessageBus.Message> blockingQueue = new LinkedBlockingDeque<>();
    final EvictionPolicyLRUWorker worker = new EvictionPolicyLRUWorker(new EvictionPolicyMessageBusImpl(blockingQueue, 1), 1);
    final LinkedCacheEntry entry2 = new LinkedCacheEntry(new CacheEntry("key2", "value2".getBytes(), 1, 1), null, null);
    final LinkedCacheEntry entry = new LinkedCacheEntry(new CacheEntry("key", "value".getBytes(), 1, 1), null, null);

    blockingQueue.offer(new EvictionPolicyMessageBus.Message(null, entry2, EvictionPolicyMessageBus.Operation.ADD));
    blockingQueue.offer(new EvictionPolicyMessageBus.Message(null, entry, EvictionPolicyMessageBus.Operation.GET));

    worker.run(() -> !blockingQueue.isEmpty());

    Assert.assertNull(entry.getNext());
    Assert.assertNull(entry2.getPrevious());

    Assert.assertEquals(entry, entry2.getNext());
    Assert.assertEquals(entry2, entry.getPrevious());
  }
}
