package cn.yachaozz.netty.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

import java.nio.ByteBuffer;
import java.nio.charset.CharsetEncoder;

import static io.netty.util.CharsetUtil.*;

/**
 * @author HouYC
 * @create 2020-10-25-13:59
 *
 * 说明：
 *  1、我们自定义一个Handel 需要继续netty规定好的某个HandlerAdapter （规范）
 *  2、这时我们自定义一个Handler，才能称为一个handler
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     *  读取数据实际（这里我们可以读取客户端发送的消息）
     *
     *  1、ChannelHandlerContext ctx, 上下文对象，含有  管道pipeline, 通道channel ，地址
     *  2、Object msg ，就是客户端发送的数据，默认Object
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        System.out.println("服务器读取线程：" + Thread.currentThread().getName());
        System.out.println("server ctx = " + ctx);
        System.out.println(" 看看channel 和 pipeline 的关系");
        Channel channel = ctx.channel();
        // 本质是一个双向链接，出栈入栈
        ChannelPipeline pipeline = ctx.pipeline();
        // 将msg 转成一个ByteBuf ，ByteBuf 是Netty提供的，不是NIO的ByteBuffer
        ByteBuf byteBuffer = (ByteBuf) msg;
        System.out.println("客户端发送的消息是：" + byteBuffer.toString(CharsetUtil.UTF_8));
        System.out.println("客户端的地址是:" + channel.remoteAddress());
    }

    /**
     * 数据读取完毕
     * @param ctx
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {

        // writeAndFlush 是 write + flush，将数据写入缓存，并刷新。 我们一般都要对发送的这个数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端帅气超向您打招呼", CharsetUtil.UTF_8));
    }

    /**
     *  处理异常，一般是需要关闭通道
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

}
