package cn.yachaozz.netty.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author HouYC
 * @create 2020-10-25-13:35
 */
public class NettyServer {

    public static void main(String[] args) {

        // 创建BossGroup 和 WorkerGroup，BossGroup 主要负责接收客户端请求，WorkerGroup处理业务请求，两个都是无限循环
        // BossGroup 和 WorkerGroup 含有的子线程数（NioEventLoop） 的个数
        // 默认实际 CUP核数 * 2
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        // 创建服务器启动对象，配置参数
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        try {
                        // 设置两个线程组
        serverBootstrap.group(bossGroup, workerGroup)
                // 使用NioSocketChannel 作为服务器的通道实现
                .channel(NioServerSocketChannel.class)
                // 设置线程队列得到链接个数
                .option(ChannelOption.SO_BACKLOG, 128)
                // 设置保持活动连接状态
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                // 创建一个通道测试对象 （匿名对象）, 给我们的workerGroup 的 EvenLoop对应的管道设置处理器
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    // 给pipeline 设置处理器
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new NettyServerHandler());
                    }
                });
        System.out.println(".....服务器 is ready.......");

        // 绑定一个端口并且同步，生成一个ChannelFuture 对象
        ChannelFuture channelFuture = serverBootstrap.bind(6668).sync();

        // 对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
