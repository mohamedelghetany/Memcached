package protocol.exception;

import javax.annotation.Nonnull;

public class CommandExecutionException extends MemCachedException {
  public CommandExecutionException(@Nonnull final Throwable cause) {
    super("Error while executing command", cause);
  }
}
