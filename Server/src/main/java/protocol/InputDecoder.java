package protocol;

import cache.Cache;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import protocol.command.Command;

// A command line always starts with the name of the command, followed by
// parameters (if any) delimited by whitespace. Command names are
// lower-case and are case-sensitive.

// ByteToMessageDecoder vs ReplayDecoder https://netty.io/4.0/api/io/netty/handler/codec/ReplayingDecoder.html
public class InputDecoder extends ReplayingDecoder<Void> {
  private static final Logger logger = LogManager.getLogger(InputDecoder.class);
  private final Cache cache;

  public InputDecoder(@Nonnull final Cache cache) {
    this.cache = cache;
  }

  @Override
  protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
    final int length = in.bytesBefore(Command.DELIMITER_SPACE);
    final byte[] bytes = new byte[length];
    in.readBytes(bytes);
    final String strCommand = new String(bytes);

    final Command command = Command.CommandFactory.createCommand(strCommand, cache);

    // Skipping 'space'
    in.skipBytes(1);

    out.add(command.decode(in));

    if (logger.isDebugEnabled()) {
      logger.debug("Received command " + command.toString());
    }
  }
}
