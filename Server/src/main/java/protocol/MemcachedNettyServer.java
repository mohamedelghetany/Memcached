package protocol;

import cache.Cache;
import cache.CacheStats;
import cache.EvictionPolicyMessageBus;
import cache.EvictionPolicyMessageBusImpl;
import cache.MemCached;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 * The main Netty Server class. Encapsulates the main function and
 * all Netty server initialization.
 *
 * One thing to look for is that we init the Netty worker pool to the number of
 * available processors returned by {@link Runtime#getRuntime().availableProcessors()}
 *
 * This is based on the examples at
 *
 * https://github.com/netty/netty/wiki/User-guide-for-4.x
 */
public class MemcachedNettyServer {
  private static final Logger logger = Logger.getLogger(MemcachedNettyServer.class);
  private final int port;
  private int availableProcessorsCount;

  public MemcachedNettyServer(final int port, int availableProcessorsCount) {
    this.port = port;
    this.availableProcessorsCount = availableProcessorsCount;
  }

  private void initAndRun() throws InterruptedException {
    logger.info("Initializing and running MemcachedNettyServer...");

    final EventLoopGroup bossGroup = new NioEventLoopGroup();
    final EventLoopGroup workerGroup = new NioEventLoopGroup(availableProcessorsCount);

    try {
      final Integer maxCacheSize = ServerProperties.maxCacheSize.get();
      final EvictionPolicyMessageBus policy = new EvictionPolicyMessageBusImpl(maxCacheSize);
      final Cache cache = new MemCached(maxCacheSize, policy);

      final ServerBootstrap serverBootstrap = new ServerBootstrap();
      serverBootstrap.group(bossGroup, workerGroup);
      serverBootstrap.channel(NioServerSocketChannel.class);
      serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
        protected void initChannel(SocketChannel ch) {
          ch.pipeline().addLast(new InputDecoder(cache));
          ch.pipeline().addLast(new OutputEncoder());
          ch.pipeline().addLast(new CommandHandler());
        }
      });
      serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
      serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

      final ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

      // Start stats reporter
      CacheStats.getInstance().initialize();

      // Run & Wait
      logger.info(String.format("Starting server on Port %d ", port));
      channelFuture.channel().closeFuture().sync();
    } finally {
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }

  public static void main(String[] args) throws InterruptedException, IOException {
    ServerProperties.initialize();

    final int port = ServerProperties.PORT.get();

    final int availableProcessorsCount = Runtime.getRuntime().availableProcessors();

    logger.debug(String.format("Found %d available processors", availableProcessorsCount));

    new MemcachedNettyServer(port, availableProcessorsCount).initAndRun();
  }
}
