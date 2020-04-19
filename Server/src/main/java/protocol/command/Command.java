package protocol.command;

import cache.Cache;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;
import protocol.exception.MemCacheException;
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
    this.cache = cache;
  }

  public abstract CommandResult execute() throws MemCacheException;

  public abstract Command decode(final ByteBuf in);

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

  @Override
  public String toString() {
    return "Command {" +
        "name=" + getType() +
        ", key='" + key + '\'' +
        '}';
  }

  public static class CommandFactory {
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
