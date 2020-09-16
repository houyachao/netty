package cn.yachaozz.netty.netty.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author HouYC
 * @create 2020-09-16-22:43
 */
public class NIOClient {

    public static void main(String[] args) throws Exception {
        //得到一个网络通道
        SocketChannel socketChannel = SocketChannel.open();
        //设置非阻塞
        socketChannel.configureBlocking(false);
        //提供服务端的ip 和端口
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6666);

        //链接服务器
        if (!socketChannel.connect(inetSocketAddress)) {
            while (!socketChannel.finishConnect()) {
                System.out.println("因为链接需要时间，客户端不会阻塞，可以做其他工作。。");
            }
        }
        //如果链接成功，就发生数据
        String str = "hello，侯亚超！";
        ByteBuffer byteBuffer = ByteBuffer.wrap(str.getBytes());
        //发送数据，将buffer数据写入channel
        socketChannel.write(byteBuffer);
        System.in.read();
    }
}
