package cache;

public final class CacheEntry {
  private final String key;
  private final int flags;
  private final int expTime;
  private final byte[] value;

  public CacheEntry(String key, byte[] value, int flags, int expTime) {
    this.value = value;
    this.key = key;
    this.flags = flags;
    this.expTime = expTime;
  }

  public byte[] getValue() {
    return value;
  }

  public String getKey() {
    return key;
  }

  public int getExpTime() {
    return expTime;
  }

  public int getFlags() {
    return flags;
  }
}
