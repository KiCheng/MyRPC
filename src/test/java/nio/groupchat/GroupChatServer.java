package nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 服务端检测用户上限、离线，并实现消息转发功能
 */
public class GroupChatServer {

    // 定义属性
    private Selector selector;
    private ServerSocketChannel listenChannel;

    private static final int PORT = 6667;

    public GroupChatServer() {
        try {
            // 开启选择器和监听通道
            selector = Selector.open();
            listenChannel = ServerSocketChannel.open();
            // 绑定端口
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            // 设置非阻塞模式
            listenChannel.configureBlocking(false);
            // channel注册到selector中
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        while (true) {
            try {
                int count = selector.select();
                // 有事件处理
                if(count > 0) {
                    // 遍历得到selectionKey集合
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        // 取出selectionKey
                        SelectionKey key = iterator.next();
                        // 监听到accept连接
                        if (key.isAcceptable()) {
                            SocketChannel sc = listenChannel.accept();
                            sc.configureBlocking(false);
                            // 将该sc注册到selector
                            sc.register(selector, SelectionKey.OP_READ);
                            System.out.println(sc.getRemoteAddress() + " 上线 ");
                        }
                        // 通道发送read事件，通道可读
                        if(key.isReadable()) {
                            readData(key);
                        }
                        iterator.remove();
                    }
                } else {
                    System.out.println("等待...");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void readData(SelectionKey key) {
        SocketChannel channel = null;
        try {
            // 根据selectionKey得到channel
            channel = (SocketChannel) key.channel();
            // 创建buffer
            ByteBuffer buffer = ByteBuffer.allocate(1024);  // buffer.array底层数组上限
            int count = channel.read(buffer);
            // 根据count的值处理
            if(count > 0) {
                // 把缓冲区的数据转成字符串
                String msg = new String(buffer.array());
                System.out.println("form客户端：");
                System.out.println(msg.trim());
                // 向其他的客户端转发消息，专门写一个方法来处理
                sendInfoToOtherClients(msg, channel);
            }
        } catch (IOException e) {
            try {
                System.out.println(channel.getRemoteAddress() + "离线了...");
                // 取消注册
                key.cancel();
                // 关闭通道
                channel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void sendInfoToOtherClients(String msg, SocketChannel sc) throws IOException {
        System.out.println("服务器转发消息中...");
        // 遍历所有注册到selector上的SocketChannel，并排除sc
        for (SelectionKey key: selector.keys()) {
            // 通过key取出对应的SocketChannel
            Channel channel = key.channel();
            if (channel instanceof SocketChannel && channel != sc) {
                SocketChannel dest = (SocketChannel) channel;
                // 将msg存储到buffer中
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                // 将buffer的数据写入通道
                dest.write(buffer);
            }
        }
    }

    public static void main(String[] args) {
        // 创建服务器对象
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();
    }
}
