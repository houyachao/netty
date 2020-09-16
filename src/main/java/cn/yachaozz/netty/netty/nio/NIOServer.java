package cn.yachaozz.netty.netty.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @author HouYC
 * @create 2020-09-16-22:11
 *
 *  NIO 非阻塞网络编程原理
 *
 *  1、当客户端连接时，会通过ServerSocketChannel 得到SocketChanel，
 *  2、Selector 进行监听 select 方法，返回有事件发生的通道的个数，
 *  3、将socketChanel 注册到Selector 上，region(Selector sel, int ops)，一个selector 上可以注册多个SocketChanel。
 *  4、注册后返回一个SelectionKey,会和该Selector 关联（集合）。
 *  5、进一步得到各个SelectionKey(有事件发生)
 *  6、在通过SelectionKEY 反向获取SocketChanel ，方法channel（）、
 *  7、可以通过 得到 channel，完成业务处理。
 */
public class NIOServer {

    public static void main(String[] args) throws Exception {
        //1、创建ServerSocketChannel -》socketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //2、得到一个Selector对象
        Selector selector = Selector.open();

        //绑定端口监听6666，在服务器端监控
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));

        //设置为非阻塞
        serverSocketChannel.configureBlocking(false);

        //把serverSocketChanel 注册到 Selector 关心事件为 OP_Access
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("啊哈哈哈哈。。。。。。。。。。。。。。。。。。。。。。");

        //循环等待客户端链接
        while (true) {

            //这里我们等待1秒，如果没有事件发生，返回
            if (selector.select(1000) == 0) {
                //没有事件发生
                System.out.println("服务器等待了1秒，无连接");
                continue;
            }
            //如果返回 > 0 ， 就获取到相关的selectionKey 集合，
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                //获取到selectionkey
                SelectionKey key = iterator.next();
                //根据key 对应的通道发生的事件做不同的事情
                if (key.isAcceptable()) {
                    //有新的客户端连接
                    //给该客户端生成一个socketChannel
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println("客户端连接成功 生成了一个 socketChanel " + socketChannel.hashCode());
                    //将该通道设为非阻塞的，因为上面设为的非阻塞，不然这里会报错
                    socketChannel.configureBlocking(false);
                    //将该socketChanel通道注册到selector，关注事件为 OP_READ，同时给socketChanel 关联一个Buffer
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }

                if (key.isReadable()) {
                    //发生OP_READ，通过反射得到对应的socketChanel
                    SocketChannel channel = (SocketChannel) key.channel();
                    //获取到对应的buffer
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    //将通道中的数据写入buffer中
                    channel.read(buffer);
                    System.out.println("from 客户端" + new String(buffer.array()));
                }

                //手动从集合中移除当前的selectorionKey，防止重复操作
                iterator.remove();
            }


        }


    }
}








