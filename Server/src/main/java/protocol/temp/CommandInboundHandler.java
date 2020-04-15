package protocol.temp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.apache.log4j.Logger;

public class CommandInboundHandler extends ChannelInboundHandlerAdapter {
  private static final Logger logger = Logger.getLogger(CommandInboundHandler.class);

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    try {
      // Do something with msg
    } finally {
      ReferenceCountUtil.release(msg);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
    logger.error("Ooops error", cause);
    ctx.close();
  }
}
