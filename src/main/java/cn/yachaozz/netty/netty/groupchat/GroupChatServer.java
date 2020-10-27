package cn.yachaozz.netty.netty.groupchat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author HouYC
 * @create 2020-10-26-21:04
 */
public class GroupChatServer {

    /**
     * 监听端口
      */
    private int port;

    public GroupChatServer(int port) {
        this.port = port;
    }

    /**
     *  编写run方法，处理客户端的请求
     */
    public void run() {

        // 创建两个线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 获取到 pipeline
                            ChannelPipeline pipeline = ch.pipeline();
                            // 向pipeline 中加入解码器
                            pipeline.addLast("decoder", new StringDecoder());
                            // 向pipeline 加入编码器
                            pipeline.addLast("encoder", new StringEncoder());

                            // 可以引入心跳检测机制
                            /**
                             * 说明。
                             * 1、IdleStateHandler 是netty提供的处理空闲状态的处理器
                             * 2、long readIdleTime 表示多长时间没有读，就会发送一个心跳检测包检测是否连接
                             * 3、long writeIdleTime 表示长时间没有写，就会发送一个心跳检测包检测是否链接
                             * 4、long alldleTime 表示多长时间没有读写，就会发送一个心跳检测包检测是否链接
                             * 5、当idlestateEvent 触发后，就会传递给管道的下一个handle去处理，通过调用（触发）下一个handle的
                             *      userEventTiggered  ，在该方法中去处理IdleStateEvent（读空闲，写空闲，读写空闲）
                             */
                            pipeline.addLast(new IdleStateHandler(3,5,2, TimeUnit.SECONDS));
                            pipeline.addLast(new MyServerHandle());


                            // 加入自己的业务处理handler
                            pipeline.addLast(new GroupChatServerHandler());
                        }
                    });

            System.out.println("netty 服务器启动");
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

            //监听关闭
            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new GroupChatServer(7000).run();
    }
}
