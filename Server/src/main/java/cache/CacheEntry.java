package cache;

import javax.annotation.Nonnull;

/**
 * Encapsulates the entry that will be cached which includes
 * key, value, flags, expTime
 *
 */
public final class CacheEntry {
  private final String key;
  private final int flags;
  private final int expTime;
  private final byte[] value;

  public CacheEntry(@Nonnull final String key, @Nonnull final byte[] value, final int flags, final int expTime) {
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
