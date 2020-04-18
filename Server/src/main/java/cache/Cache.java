package cache;

import javax.annotation.Nonnull;

public interface Cache {
  boolean contains(@Nonnull final CacheEntry entry);

  CacheEntry get(@Nonnull final String key);

  boolean set(@Nonnull final CacheEntry entry);

  boolean add(@Nonnull final CacheEntry entry);

  boolean replace(@Nonnull final CacheEntry entry);

  boolean delete(@Nonnull final CacheEntry entry);
}
