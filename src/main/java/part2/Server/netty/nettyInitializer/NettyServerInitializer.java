package part2.Server.netty.nettyInitializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.AllArgsConstructor;
import part2.Server.provider.ServiceProvider;
import part2.Server.netty.handler.NettyServerHandler;

@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    private ServiceProvider serviceProvider;

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        // 消息格式: 长度、消息体，可以解决TCP的粘包问题
        pipeline.addLast(
                new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,4,0,4));
        // 计算当前待发送消息的长度，写入到前4个字节中
        pipeline.addLast(new LengthFieldPrepender(4));

        // 编码器，netty中的objectEncoder
        pipeline.addLast(new ObjectEncoder());
        // 解码器，netty中的objectDecoder
        // 在ObjectDecoder中传入一个 ClassResolver 对象，用于解析类名并加载相应类
        pipeline.addLast(new ObjectDecoder(new ClassResolver() {
            @Override
            public Class<?> resolve(String className) throws ClassNotFoundException {
                return Class.forName(className);
            }
        }));

        pipeline.addLast(new NettyServerHandler(serviceProvider));
    }
}
