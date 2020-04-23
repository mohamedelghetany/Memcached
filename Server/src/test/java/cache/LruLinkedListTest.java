package cache;

import org.junit.Assert;
import org.junit.Test;

public class LruLinkedListTest {

  @Test
  public void testMoveToFirstUpdateListCorrectly() {
    LinkedCacheEntry entry1 = new LinkedCacheEntry(new CacheEntry("foo", "bar".getBytes(), 1, 1), null, null);
    LinkedCacheEntry entry2 = new LinkedCacheEntry(new CacheEntry("x", "y".getBytes(), 1, 1), null, null);

    LruLinkedList list = new LruLinkedList();
    list.moveToFirst(entry2);
    list.moveToFirst(entry1);

    Assert.assertNull(entry1.getNext());
    Assert.assertEquals(entry2, entry1.getPrevious());

    Assert.assertNull(entry2.getPrevious());
    Assert.assertEquals(entry1, entry2.getNext());
  }

  @Test
  public void testRemoveLastRemovesTheLastEntry() {
    LinkedCacheEntry entry1 = new LinkedCacheEntry(new CacheEntry("foo", "bar".getBytes(), 1, 1), null, null);
    LinkedCacheEntry entry2 = new LinkedCacheEntry(new CacheEntry("x", "y".getBytes(), 1, 1), null, null);

    LruLinkedList list = new LruLinkedList();
    list.moveToFirst(entry2);
    list.moveToFirst(entry1);
    list.removeLast();

    Assert.assertNull(entry1.getPrevious());
    Assert.assertNull(entry1.getNext());
    Assert.assertNull(entry2.getNext());
    Assert.assertNull(entry2.getPrevious());
  }
}
