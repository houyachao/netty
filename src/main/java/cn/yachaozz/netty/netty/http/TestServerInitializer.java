package cn.yachaozz.netty.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author HouYC
 * @create 2020-10-25-16:53
 */
public class TestServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 得到管道
        ChannelPipeline pipeline = ch.pipeline();
        // 加入一个netty 提供的httpServerCodec   codec => [coder - decoder]
        // HttpServerCodec 说明
        // 1.HttpServerCodec 是netty 提供的处理http的 编解码器
        pipeline.addLast("MyTestServerInitializer", new HttpServerCodec());
        // 2.增加一个自定义的handler
        pipeline.addLast("MyTestHttpServerHandler", new TestHttpServerHandler());
    }
}
