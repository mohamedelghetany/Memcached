package protocol.exception;

public class CommandDecodingException extends MemCachedException {
  public CommandDecodingException(final Throwable cause) {
    super("Error Decoding command", cause);
  }
}
