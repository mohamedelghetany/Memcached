package cache;

import org.apache.log4j.Logger;

public final class LruLinkedList {
  private static final Logger logger = Logger.getLogger(LruLinkedList.class);
  private LinkedCacheEntry head = null;
  private LinkedCacheEntry tail = null;

  public void moveToFirst(final LinkedCacheEntry entry) {
    if (head == null) {
      head = entry;
      tail = head;
      return;
    }

    if(head == entry) {
      return;
    }

    final LinkedCacheEntry previous = entry.getPrevious();

    if(previous != null) {
      previous.setNext(entry.getNext());
    }

    if(tail == entry) {
      tail = entry.getNext();
    }

    entry.setNext(null);
    entry.setPrevious(head);
    head.setNext(entry);
    head = entry;
  }

  public LinkedCacheEntry removeLast() {
    final LinkedCacheEntry last = tail;
    tail = tail.getNext();

    tail.setPrevious(null);
    last.setNext(null);
    last.setPrevious(null);

    return last;
  }
}
