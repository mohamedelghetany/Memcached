package protocol.command;

import cache.Cache;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;
import protocol.exception.MemCacheException;
import protocol.exception.UnsupportedCommandException;

public abstract class Command {
  public static final byte DELIMITER_END_OF_LINE = (byte) '\r';
  public static final byte DELIMITER_SPACE = (byte) ' ';

  private final String name;
  private final Cache cache;
  private String key;

  public Command(@Nonnull final String name, @Nonnull final Cache cache) {
    this.name = name;
    this.cache = cache;
  }

  public abstract CommandResult execute() throws MemCacheException;

  public abstract Command decode(final ByteBuf in);

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public String getName() {
    return name;
  }

  public Cache getCache() {
    return cache;
  }

  @Override
  public String toString() {
    return "Command {" +
        "name=" + name +
        ", key='" + key + '\'' +
        '}';
  }

  public static class CommandFactory {
    public static Command createCommand(@Nonnull final String command, @Nonnull final Cache cache) throws UnsupportedCommandException {

      if (CommandType.GET.getStrName().equals(command)) {
        return new GetCommand(cache);
      } else if (CommandType.SET.getStrName().equals(command)) {
        return new SetCommand(cache);
      } else {
        throw new UnsupportedCommandException(command);
      }
    }
  }

  public enum CommandType {
    GET("get"),
    SET("set");

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
