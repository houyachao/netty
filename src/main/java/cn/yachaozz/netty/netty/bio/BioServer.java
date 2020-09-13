package cn.yachaozz.netty.netty.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author HouYC
 * @create 2020-09-13-21:17
 *
 * 同步阻塞IO，服务器实现模式为一个链接一个线程，即客户端有链接请求时服务器端就需要启动一个线程进行处理，
 * 如果这个连接不做任何事情会造成不必要的线程开销.
 *
 *
 * 使用线程池机制，创建一个线程，如果有客户端链接，就创建一个线程，与之通信（可以单独写一个方法）
 *
 * cmd : telnet 127.0.0.1 6666
 *       ctrl ]
 *        send houyachao
 *
 *
 *  控制台输出：
 *      服务器都启动了。。。
 *      线程信息 id =1 名字 name = main
 *      我要监听，等待一个客户端连接成功为止，一直处于阻塞中。。。
 *      连接到一个客户端。。。。
 *      线程信息 id =1 名字 name = main
 *      我要监听，等待一个客户端连接成功为止，一直处于阻塞中。。。
 *      线程信息 id = 11 名字 = pool-1-thread-1
 *      read.......
 *      我要等待客户端发送信息，并一直处于阻塞中。。。。
 *      houyachao
 *      线程信息 id = 11 名字 = pool-1-thread-1
 *      read.......
 *      我要等待客户端发送信息，并一直处于阻塞中。。。。
 */
public class BioServer {

    public static void main(String[] args) throws Exception {

        //1、创建一个线程
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();

        //2、创建ServerSocket, 监听端口6666
        ServerSocket serverSocket = new ServerSocket(6666);

        System.out.println("服务器都启动了。。。");
        while (true) {

            System.out.println("线程信息 id =" + Thread.currentThread().getId() + " 名字 name = " + Thread.currentThread().getName());

            //监听等待客户端连接
            System.out.println("我要监听，等待一个客户端连接成功为止，一直处于阻塞中。。。");

            final Socket socket = serverSocket.accept();

            System.out.println("连接到一个客户端。。。。");

            //就创建一个线程，与之通讯
            newCachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    //可以和客户端通讯
                    handler(socket);
                }
            });
        }

    }

    /**
     * 编写一个handler 方法，与客户端通讯
     * @param socket
     */
    private static  void handler(Socket socket) {

        try {
            byte[] bytes = new byte[1024];
            //通过socket 获取输入流
            InputStream inputStream = socket.getInputStream();

            while (true) {

                System.out.println("线程信息 id = " + Thread.currentThread().getId() + " 名字 = " + Thread.currentThread().getName());
                System.out.println("read.......");
                System.out.println("我要等待客户端发送信息，并一直处于阻塞中。。。。");
                int read = inputStream.read(bytes);
                if (read != -1) {
                    //输出客户端发送的信息
                    System.out.println(new String(bytes, 0, read));
                } else {
                    //读取完成
                    System.out.println("读取完成。。。。");
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("关闭和Clint 连接");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
