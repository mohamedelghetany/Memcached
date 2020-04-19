package protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.log4j.Logger;
import protocol.command.Command;

/**
 * Encode the result to be send back to the user.
 *
 * The result of executing each command is encapsulated in
 * {@link protocol.command.Command.CommandResult} which already has
 * a {@link Byte[]} as a result.
 */
public class OutputEncoder extends MessageToByteEncoder<Command.CommandResult> {
  private static final Logger logger = Logger.getLogger(OutputEncoder.class);

  @Override
  protected void encode(ChannelHandlerContext ctx, Command.CommandResult commandResult, ByteBuf out) {
    logger.debug(commandResult.toString());

    out.writeBytes(commandResult.getResultValue());
  }
}
