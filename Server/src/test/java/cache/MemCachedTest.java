package cache;

import java.util.concurrent.LinkedBlockingDeque;
import org.junit.Assert;
import org.junit.Test;

public class MemCachedTest {

  @Test
  public void testSet() {
    final LinkedBlockingDeque<EvictionPolicyMessageBus.Message> blockingQueue = new LinkedBlockingDeque<>();
    final Cache cache = new MemCached(new EvictionPolicyMessageBusImpl(blockingQueue, 2), 2);

    Assert.assertTrue(cache.set(new CacheEntry("foo", "bar".getBytes(), 1, 1)));
    Assert.assertEquals("bar", new String(cache.get("foo").getValue()));
  }

  @Test
  public void testAdd() {
    final LinkedBlockingDeque<EvictionPolicyMessageBus.Message> blockingQueue = new LinkedBlockingDeque<>();
    final Cache cache = new MemCached(new EvictionPolicyMessageBusImpl(blockingQueue, 2), 2);

    Assert.assertTrue(cache.add(new CacheEntry("foo", "bar".getBytes(), 1, 1)));
    Assert.assertEquals("bar", new String(cache.get("foo").getValue()));
  }

  @Test
  public void testReplace() {
    final LinkedBlockingDeque<EvictionPolicyMessageBus.Message> blockingQueue = new LinkedBlockingDeque<>();
    final Cache cache = new MemCached(new EvictionPolicyMessageBusImpl(blockingQueue, 2), 2);
    cache.set(new CacheEntry("foo", "value".getBytes(), 1, 1));
    Assert.assertTrue(cache.replace(new CacheEntry("foo", "bar".getBytes(), 1, 1)));
    Assert.assertEquals("bar", new String(cache.get("foo").getValue()));
  }

  @Test
  public void testReplaceReturnFalseIfKeyDoesNotExist() {
    final LinkedBlockingDeque<EvictionPolicyMessageBus.Message> blockingQueue = new LinkedBlockingDeque<>();
    final Cache cache = new MemCached(new EvictionPolicyMessageBusImpl(blockingQueue, 2), 2);

    Assert.assertFalse(cache.replace(new CacheEntry("key", "newValue".getBytes(), 1, 1)));
    Assert.assertNull(cache.get("key"));
  }

  @Test
  public void testAddReturnFalseIfKeyDoesExist() {
    final LinkedBlockingDeque<EvictionPolicyMessageBus.Message> blockingQueue = new LinkedBlockingDeque<>();
    final Cache cache = new MemCached(new EvictionPolicyMessageBusImpl(blockingQueue, 2), 2);
    cache.set(new CacheEntry("key", "value".getBytes(), 1, 1));

    Assert.assertFalse(cache.add(new CacheEntry("key", "newValue".getBytes(), 1, 1)));
    Assert.assertEquals("value", new String(cache.get("key").getValue()));
  }

  @Test
  public void testCacheLeastRecentlyEntryGetsEvicted() throws InterruptedException {
    final LinkedBlockingDeque<EvictionPolicyMessageBus.Message> blockingQueue = new LinkedBlockingDeque<>();
    EvictionPolicyMessageBus messageBus = new EvictionPolicyMessageBusImpl(blockingQueue, 2);
    EvictionPolicyLRUWorker evictionPolicyLRUWorker = new EvictionPolicyLRUWorker(messageBus, 2);
    Cache cache = new MemCached(messageBus, 2);

    CacheEntry entry1 = new CacheEntry("key1", "value1".getBytes(), 1, 1);
    CacheEntry entry2 = new CacheEntry("key2", "value2".getBytes(), 1, 1);
    CacheEntry entry3 = new CacheEntry("key3", "value3".getBytes(), 1, 1);

    cache.add(entry1);
    cache.add(entry2);
    cache.add(entry3);

    evictionPolicyLRUWorker.run(() -> !blockingQueue.isEmpty());

    Assert.assertNull(cache.get("entry1"));
    Assert.assertEquals("value2", new String(cache.get("key2").getValue()));
    Assert.assertEquals("value3", new String(cache.get("key3").getValue()));
  }
}
