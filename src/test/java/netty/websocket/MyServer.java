package netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.zookeeper.Op;

/**
 * Http 是无状态的，实现基于 Websocket 的长连接全双工交互
 * 而且客户端和服务端可以相互感知，一侧关闭了另一侧会感知
 */

public class MyServer {
    public static void main(String[] args) {

        // 创建两个线程组
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            // 基于http协议，使用http的编码和解码器
                            pipeline.addLast(new HttpServerCodec());
                            // 以块的方式读写
                            pipeline.addLast(new ChunkedWriteHandler());

                            /**
                             * 浏览器发送大量数据时就会发出多次http请求，因为http数据时分段的，httpObjectAggregator可以将多个段合并
                             */
                            pipeline.addLast(new HttpObjectAggregator(8192));

                            /**
                             * 对应Websocket，数据以frame形式传递
                             * WebSocketServerProtocolHandler核心功能将http协议升级为ws协议，保持长连接
                             */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello2"));

                            // 自定义handler
                            pipeline.addLast(new MyTextWebSocketFrameHandler());
                        }
                    });

            // 启动服务器
            ChannelFuture channelFuture = bootstrap.bind(6668).sync();
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
