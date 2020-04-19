package protocol.exception;

/**
 * All Maxcache exceptions should extend this class.
 */
public abstract class MemCacheException extends Exception {
  public MemCacheException(final String message) {
    super(message);
  }
}
