package protocol.command;

import cache.Cache;
import cache.CacheEntry;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;

public class SetCommand extends Command {
  private int dataSize;
  private byte[] data;
  private int flags;
  private int expTime;

  public SetCommand(@Nonnull final Cache cache) {
    super(cache);
  }

  @Override
  public CommandResult executeInternal() {
    final CacheEntry entry = new CacheEntry(getKey(), getData(), getFlags(), getExpTime());
    final boolean setResult = getCache().set(entry);
    final String strResult = setResult ? STORED : SERVER_ERROR;

    return new CommandResult(getType(), strResult.getBytes());
  }

  /**
   * 1- SET <key> <flags> <exptime> <bytes> [noreply]\r\n
   * 2- <data block>\r\n
   */
  @Override
  public Command decodeInternal(ByteBuf in) {
    // Key
    int length = in.bytesBefore(DELIMITER_SPACE);
    setKey(readBytesHelper(in, length));

    // Skipping 'space'
    in.skipBytes(1);

    // Flags
    length = in.bytesBefore(DELIMITER_SPACE);
    setFlags(Integer.parseInt(readBytesHelper(in, length)));

    // Skipping 'space'
    in.skipBytes(1);

    // expTime
    length = in.bytesBefore(DELIMITER_SPACE);
    setExpTime(Integer.parseInt(readBytesHelper(in, length)));

    // Skipping 'space'
    in.skipBytes(1);

    // data size
    length = in.bytesBefore(DELIMITER_END_OF_LINE);
    int dataSize = Integer.parseInt(readBytesHelper(in, length));

    setDataSize(dataSize);

    // Skipping '\r\n' as end of 1st line
    in.skipBytes(2);

    // Data
    byte[] data = new byte[dataSize];
    in.readBytes(data);
    setData(data);

    // Skipping '\r\n' as end of the data
    in.skipBytes(2);

    return this;
  }


  @Override
  public CommandType getType() {
    return CommandType.SET;
  }

  private void setExpTime(final int expTime) {
    this.expTime = expTime;
  }

  private void setFlags(final int flags) {
    this.flags = flags;
  }

  public byte[] getData() {
    return data;
  }

  public int getDataSize() {
    return dataSize;
  }

  public int getFlags() {
    return flags;
  }

  public int getExpTime() {
    return expTime;
  }

  @Override
  public String toString() {
    return "Command {" +
        "name=" + getType() +
        ", key='" + getKey() + '\'' +
        ", DataSize='" + getDataSize() + '\'' +
        ", Data='" + new String(getData()) + '\'' +
        '}';
  }

  private void setDataSize(int dataSize) {
    this.dataSize = dataSize;
  }

  private void setData(final byte[] data) {
    this.data = data;
  }
}
