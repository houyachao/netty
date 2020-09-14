package cn.yachaozz.netty.netty.nio;

import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author HouYC
 * @create 2020-09-14-22:10
 *
 * mappedByteBuffer 可让文件直接在内存（堆外内存）中修改，操作系统不需要拷贝一次
 */
public class MappedByteBufferTest {
    public static void main(String[] args) throws Exception {

        RandomAccessFile randomAccessFile = new RandomAccessFile("1.txt", "rw");

        FileChannel channel = randomAccessFile.getChannel();

        /**
         * map 参数1： 指定该通道是可以读写的
         *      参数2：可以直接修改的起始位置
         *      参数3：是映射到内存的大小（不是索引的位置），即将1.txt 的多少个字节映射到内存
         *      可以直接修改的范围0-5
         *      实际类型DirectByteBuffer
         */
        MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);
        map.put(1,(byte) 'a');
        map.put(3,(byte) 'Y');

        channel.close();
        System.out.println("写入完毕");
    }
}
