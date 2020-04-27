package protocol.command;

import cache.Cache;
import cache.CacheEntry;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;
import javax.annotation.Nonnull;

public class GetCommand extends Command {

  public GetCommand(@Nonnull final Cache cache) {
    super(cache);
  }

  @Override
  protected CommandResult executeInternal() {
    final CacheEntry entry = getCache().get(getKey());

    if (entry != null) {
      final byte[] header = String.format("VALUE %s %d %d\r\n", entry.getKey(), entry.getFlags(), entry.getValue().length).getBytes();
      final byte[] data = entry.getValue();
      final byte[] end = "\r\nEND\n".getBytes();

      final ByteBuffer buff = ByteBuffer.wrap(new byte[header.length + data.length + end.length]);
      buff.put(header);
      buff.put(data);
      buff.put(end);

      return new CommandResult(getType(), buff.array());
    } else {
      return new CommandResult(getType(), "END\r\n".getBytes());
    }
  }

  /**
   * Example: get <key>*\r\n
   */
  @Override
  protected Command decodeInternal(@Nonnull final ByteBuf in) {
    Preconditions.checkArgument(in != null, "Input ByteBuf can not be null");

    final int length = in.bytesBefore(DELIMITER_END_OF_LINE);
    setKey(readBytesHelper(in, length));

    in.skipBytes(2); // \r\n

    return this;
  }

  @Override
  public CommandType getType() {
    return CommandType.GET;
  }
}
