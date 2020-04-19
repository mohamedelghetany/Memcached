package protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import protocol.command.Command;

/**
 * Handles cache commands
 */
public class CommandHandler extends SimpleChannelInboundHandler<Command> {
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Command command) throws Exception {
     ctx.writeAndFlush(command.execute());
  }
}
