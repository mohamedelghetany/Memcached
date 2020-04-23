package protocol.exception;

/**
 * All Maxcache exceptions should extend this class.
 */
public abstract class MemCachedException extends Exception {
  public MemCachedException(final String message) {
    super(message);
  }

  public MemCachedException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
