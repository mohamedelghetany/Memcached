package cache;

import javax.annotation.Nonnull;

/**
 * Basic interface for all the operations our cache should support
 */
public interface Cache {

  /**
   * Returns <tt>true</tt> if this map contains a mapping for the specified
   * key.
   *
   * @param entry key whose presence in this map is to be tested
   * @return <tt>true</tt> if this map contains a mapping for the specified
   * key\
   */
  boolean contains(@Nonnull final CacheEntry entry);

  /**
   * Returns the value to which the specified key is mapped,
   * or {@code null} if this map contains no mapping for the key.
   *
   * @param key the key whose associated value is to be returned
   * @return the value to which the specified key is mapped, or
   * {@code null} if this map contains no mapping for the key
   */
  CacheEntry get(@Nonnull final String key);

  /**
   * Associates the specified value with the specified key in this map
   * If the map previously contained a mapping for the key,
   * the old value is replaced by the specified value.
   *
   * @param entry key with which the specified value is to be associated
   * @return <tt>true</tt> if operation succeeded, <tt>false</tt> otherwise
   */
  boolean set(@Nonnull final CacheEntry entry);

  /**
   * Associates the specified value with the specified key in this map
   * If the map previously contained a mapping for the key, then this function
   * will return false and the value won't be updated
   *
   * @param entry key with which the specified value is to be associated
   * @return <tt>true</tt> if operation succeeded and the value have been added,
   * <tt>false</tt> if the key already exists
   */
  boolean add(@Nonnull final CacheEntry entry);

  /**
   * Replace the existing entry with the given entry
   * If the map doesn't contain the entry, then return false
   *
   * @param entry key with which the specified value is to be associated
   * @return <tt>true</tt> if operation succeeded, <tt>false</tt> if the map doesn't
   * contain the given entry
   */
  boolean replace(@Nonnull final CacheEntry entry);

  boolean delete(@Nonnull final CacheEntry entry);
}
