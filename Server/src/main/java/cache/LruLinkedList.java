package cache;

public final class LruLinkedList {
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

    entry.setNext(null);
    entry.setPrevious(head);
    head.setNext(entry);
    head = entry;
  }

  public LinkedCacheEntry removeLast() {
    final LinkedCacheEntry last = tail;
    tail = tail.getNext();
    last.setNext(null);

    return last;
  }

  private boolean isEmpty() {
    return head == null;
  }
}
