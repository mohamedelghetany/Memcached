package protocol.exception;

public abstract class MemCacheException extends Exception {
  public MemCacheException(final String message) {
    super(message);
  }

  public MemCacheException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
