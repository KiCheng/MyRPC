package netty.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class NettyServer {
    public static void main(String[] args) {
        /**
         * 创建BossGroup和WorkerGroup
         * 1. 创建两个线程组，都是无线循环
         * 2. bossGroup只是处理连接请求，真正和客户端业务处理会交给workerGroup完成
         * 3. bossGroup和workerGroup含有的子线程NioEventLoop个数默认 cpu核数*2
         */
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 创建服务器端的启动对象，配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            // 配置
            bootstrap.group(bossGroup, workerGroup)  // 设置两个线程组
                    .channel(NioServerSocketChannel.class)  // 使用NioSocketChannel作为服务器的通道
                    .option(ChannelOption.SO_BACKLOG, 128)  // 设置线程队列得到连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true)  // 设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {  // 创建一个通道初始化对象（匿名对象）
                        // 给pipeline设置处理器
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            System.out.println("客户socketChannel hashcode=" + socketChannel.hashCode());
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }
                    });  // 给workerGroup的EventLoop对应的管道设置处理器
            System.out.println("...服务器 is ready...");

            // 绑定一个端口并且同步，生成一个channelFuture对象
            // 启动服务器并绑定端口
            ChannelFuture channelFuture = bootstrap.bind(6668).sync();
            // 给ChannelFuture注册监听器，监控我们关心的事件
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (channelFuture.isSuccess()) {
                        System.out.println("监听端口 6668 成功");
                    } else {
                        System.out.println("监听端口 6668 失败");
                    }
                }
            });

            // 对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
