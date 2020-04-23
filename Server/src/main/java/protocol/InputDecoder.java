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
import protocol.exception.MemCachedException;
import protocol.exception.UnsupportedCommandException;

/**
 * Decode User's input command.
 *
 * The command should always starts with the name of the command, followed by
 * parameters (if any) delimited by whitespace. Command names are
 * lower-case and are case-sensitive.
 *
 * The main task of this class is to parse the "Command", Create an instance of
 * the command using {@link protocol.command.Command.CommandFactory} (If valid and supported Command)
 * Then pass teh rest of the {@link ByteBuf} to the command itself since each command
 * encapsulates the logic of parsing it's parameters!
 *
 * {@link InputDecoder#decode(ChannelHandlerContext, ByteBuf, List)} throws
 * {@link UnsupportedCommandException} in case of unknown command
 *
 * This class extends {@link ReplayingDecoder} for simplicity. I was considering extending
 * {@link io.netty.handler.codec.ByteToMessageDecoder} but The biggest difference between ReplayingDecoder and ByteToMessageDecoder
 * is that ReplayingDecoder allows you to implement the decode() and decodeLast() methods just like all
 * required bytes were received already, rather than checking the availability of the required bytes.
 * Off course this simplicity doesn't come for free. There is a slight performance hit.
 *
 * More details: https://netty.io/4.0/api/io/netty/handler/codec/ReplayingDecoder.html
 */
public class InputDecoder extends ReplayingDecoder<Void> {
  private static final Logger logger = LogManager.getLogger(InputDecoder.class);
  private final Cache cache;

  public InputDecoder(@Nonnull final Cache cache) {
    this.cache = cache;
  }

  @Override
  protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws MemCachedException {
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
