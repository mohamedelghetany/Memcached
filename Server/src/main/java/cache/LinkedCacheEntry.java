package cache;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class LinkedCacheEntry {
  private LinkedCacheEntry next;
  private LinkedCacheEntry previous;
  private final CacheEntry entry;

  public LinkedCacheEntry(@Nonnull final CacheEntry entry, @Nullable final LinkedCacheEntry next, @Nullable final LinkedCacheEntry previous) {
    this.next = next;
    this.previous = previous;
    this.entry = entry;
  }

  public CacheEntry getEntry() {
    return entry;
  }

  @Override
  public String toString() {
    return new String(entry.getValue());
  }

  public LinkedCacheEntry getNext() {
    return next;
  }

  public LinkedCacheEntry getPrevious() {
    return previous;
  }

  public void setNext(LinkedCacheEntry next) {
    this.next = next;
  }

  public void setPrevious(LinkedCacheEntry previous) {
    this.previous = previous;
  }
}
