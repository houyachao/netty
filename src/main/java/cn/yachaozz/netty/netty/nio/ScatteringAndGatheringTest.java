package cn.yachaozz.netty.netty.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * @author HouYC
 * @create 2020-09-14-22:51
 *
 * Scattering  将数据写入到buffer时，可以采用buffer 数组，依次写入 【分散】
 * Gathering   从buffer中读取数据，可以采用buffer数组，依次读取 【聚合】
 */
public class ScatteringAndGatheringTest {

    public static void main(String[] args) throws Exception {
        //使用ServerSocketChanel 和 SocketChanel 网络
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(7000);

        //绑定端口到socket，并启动
        serverSocketChannel.socket().bind(inetSocketAddress);

        //创建buffer数组
        ByteBuffer[] byteBuffers = new ByteBuffer[2];
        byteBuffers[0] = ByteBuffer.allocate(5);
        byteBuffers[1] = ByteBuffer.allocate(3);

        //等待客户端连接
        SocketChannel socketChannel = serverSocketChannel.accept();
        int messageLength = 8;
        //循环读取
        while (true) {
             int byteRead = 0;

             while (byteRead < messageLength) {
                 long read = socketChannel.read(byteBuffers);
                 //累计读取字节数
                 byteRead += read;
                 System.out.println("byteRead=" + byteRead);
                 Arrays.asList(byteBuffers).stream().map(buffer -> "postion =" +buffer.position() + ",limit = "+
                         buffer.limit()).forEach(System.out::println);

             }

             //将所有的buffer进行反转
            Arrays.asList(byteBuffers).stream().forEach(byteBuffer -> byteBuffer.flip());
            //将数据读取出来
            long byteWrite = 0;
            while (byteWrite < messageLength) {
                long write = socketChannel.write(byteBuffers);
                byteWrite += write;
            }

            //将所有的buffer进行clear
            Arrays.asList(byteBuffers).forEach(byteBuffer -> byteBuffer.clear());
            System.out.println("byteRead =" + byteRead + "byteWrite = "+ byteWrite + ", messageLength = " + messageLength);
        }


    }
}
