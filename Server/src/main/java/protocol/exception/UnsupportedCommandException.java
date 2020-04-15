package protocol.exception;

public class UnsupportedCommandException extends MemCacheException {
  public UnsupportedCommandException(final String command) {
    super("Unsupported command " + command);
  }
}
