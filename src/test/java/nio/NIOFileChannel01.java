package nio;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 利用 buffer 和 FileChannel，将 "hello,尚硅谷" 写入到 file01.txt 中
 */
public class NIOFileChannel01 {
    public static void main(String[] args) throws IOException {
        String str = "hello, 尚硅谷";

        FileOutputStream fis = new FileOutputStream("file01.txt");
        FileChannel channel = fis.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);  // 静态工厂方法

        buffer.put(str.getBytes());

        buffer.flip();

        channel.write(buffer);
        fis.close();
    }
}
