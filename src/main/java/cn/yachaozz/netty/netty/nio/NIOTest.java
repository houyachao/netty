package cn.yachaozz.netty.netty.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author HouYC
 * @create 2020-09-14-21:04
 *
 *      read() ,将通道数据读取到buffer中
 *      write()， 将buffer中的数据写入通道中
 *
 * 使用NIO 实现 文件拷贝
 */
public class NIOTest {

    public static void main(String[] args) throws Exception {

        FileInputStream fileInputStream = new FileInputStream("1.txt");
        //创建一个写入通道
        FileChannel inputStreamChannel01 = fileInputStream.getChannel();

        FileOutputStream fileOutputStream = new FileOutputStream("2.txt");
        //创建一个读入通道
        FileChannel outputStreamChannel = fileOutputStream.getChannel();

        //创建一个缓存区  这个值的大小设置，为每次可读入的数据的大小
        ByteBuffer byteBuffer = ByteBuffer.allocate(1);

        while (true) {

            //将缓存区中的position 置为0，不然在每次反转循环读写的时候，会导致position 和 limit 相当，每次读取的都为0
            byteBuffer.clear();
            //将通道中的数据 读取数据到buffer中
            int read = inputStreamChannel01.read(byteBuffer);
            if (read == -1) {
                break;
            }

            //将缓存区反转，上面是写，下面是将buffer中的数据写入通道中
            byteBuffer.flip();
            outputStreamChannel.write(byteBuffer);
        }

        outputStreamChannel.close();
        inputStreamChannel01.close();


    }
}
