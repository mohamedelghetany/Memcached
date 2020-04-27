package protocol.command;

import cache.Cache;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;
import protocol.exception.CommandDecodingException;
import protocol.exception.CommandExecutionException;
import protocol.exception.MemCachedException;
import protocol.exception.UnsupportedCommandException;

/**
 * Base class for all supported Commands.
 */
public abstract class Command {
  public static final byte DELIMITER_END_OF_LINE = (byte) '\r';
  public static final byte DELIMITER_SPACE = (byte) ' ';
  protected final static String STORED = "STORED\r\n";
  protected final static String SERVER_ERROR ="SERVER_ERROR\r\n";
  protected static final String NOT_STORED = "NOT_STORED\r\n";;

  private final Cache cache;
  private String key;

  public Command(@Nonnull final Cache cache) {
    Preconditions.checkArgument(cache != null, "Input Cache can not be null");

    this.cache = cache;
  }

  public CommandResult execute() throws MemCachedException {
    try {
      return executeInternal();
    }catch (final Exception e) {
      throw new CommandExecutionException(e);
    }
  }

  public Command decode(@Nonnull final ByteBuf in) throws MemCachedException {
    try {
      return decodeInternal(in);
    }catch (final Exception e) {
      throw new CommandDecodingException(e);
    }
  }

  protected abstract CommandResult executeInternal();

  protected abstract Command decodeInternal(@Nonnull final ByteBuf in);

  public abstract CommandType getType();

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public Cache getCache() {
    return cache;
  }

  protected String readBytesHelper(final ByteBuf in, final int length) {
    final byte[] bytes = new byte[length];
    in.readBytes(bytes);

    return new String(bytes).trim();
  }

  @Override
  public String toString() {
    return "Command {" +
        "name=" + getType() +
        ", key='" + key + '\'' +
        '}';
  }

  /**
   * Simple Factory to create a command
   */
  public static class CommandFactory {
    /**
     * Create a command based on the input String command.
     * Input string has to match on eof enum {@link CommandType#getStrName()} values,
     * otherwise {@link CommandFactory#createCommand(String, Cache)} will fail with
     * {@link UnsupportedCommandException}
     *
     * @param command in string format. It has to match one of {@link CommandType#getStrName()}
     * @param cache the cache that this command will be executed on
     * @return {@link Command} object
     * @throws UnsupportedCommandException if command doesn't exists
     */
    public static Command createCommand(@Nonnull final String command, @Nonnull final Cache cache) throws UnsupportedCommandException {

      if (CommandType.GET.getStrName().equals(command)) {
        return new GetCommand(cache);
      } else if (CommandType.SET.getStrName().equals(command)) {
        return new SetCommand(cache);
      } else if (CommandType.ADD.getStrName().equals(command)) {
        return new AddCommand(cache);
      } else if (CommandType.REPLACE.getStrName().equals(command)) {
        return new ReplaceCommand(cache);
      } else {
        throw new UnsupportedCommandException(command);
      }
    }
  }

  /**
   * Enum represents the command types supported
   * by the server
   */
  public enum CommandType {
    GET("get"),
    SET("set"),
    ADD("add"),
    REPLACE("replace");

    private final String strName;

    CommandType(final String strName) {
      this.strName = strName;
    }

    public String getStrName() {
      return strName;
    }
  }

  /**
   * Encapsulate the result of executing a command
   */
  public static class CommandResult {
    private final CommandType type;
    private final byte[] resultValue;

    protected CommandResult(final CommandType type, final byte[] resultValue) {
      this.type = type;
      this.resultValue = resultValue;
    }

    public CommandType getType() {
      return type;
    }

    public byte[] getResultValue() {
      return resultValue;
    }

    @Override
    public String toString() {
      return "CommandResult: {" +
          " Type: " + getType() +
          " ResultValue:  " + new String(getResultValue()) +
          "}";
    }
  }
}
