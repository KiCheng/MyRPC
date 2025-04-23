package nio;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 利用 buffer 和 FileChannel，将 file01.txt 数据读取到程序中
 */
public class NIOFileChannel02 {
    public static void main(String[] args) throws Exception {
        File file = new File("file01.txt");

        FileInputStream fis = new FileInputStream(file);
        FileChannel channel = fis.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate((int) file.length());

        int read = channel.read(buffer);
        if(read != -1) {
            System.out.println(new String(buffer.array()));  // 打印内存块保存的内容
        }
        fis.close();
    }
}
