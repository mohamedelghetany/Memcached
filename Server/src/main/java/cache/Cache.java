package cache;

import javax.annotation.Nonnull;

public interface Cache {
  boolean contains(@Nonnull final String key);

  CacheEntry get(@Nonnull final String key);

  boolean set(@Nonnull final CacheEntry entry);
}
