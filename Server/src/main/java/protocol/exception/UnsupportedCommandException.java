package protocol.exception;

public class UnsupportedCommandException extends MemCachedException {
  public UnsupportedCommandException(final String command) {
    super("Unsupported command " + command);
  }
}
