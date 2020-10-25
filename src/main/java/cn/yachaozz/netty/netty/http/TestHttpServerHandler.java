package cn.yachaozz.netty.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * @author HouYC
 * @create 2020-10-25-16:58
 *
 * 说明：
 *  1.SimpleChannelInboundHandler 是 ChannelInboundHandlerAdapter 实现子类
 *  2.HttpObject 客户端和服务器端相互通讯的数据被封装成HttpObject
 *
 */
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    /**
     *  读取客户端数据
     * @param ctx
     * @param httpObject
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject httpObject) throws Exception {
        // 判断msg 是不是httpRequest
        if (httpObject instanceof HttpRequest) {
            System.out.println("pipeline hashcode =" + ctx.pipeline().hashCode() + "TestHttpServerHandler hashcode =" + this.hashCode());
            System.out.println("msg 类型 = " + httpObject.getClass());
            System.out.println("客户端地址 = " + ctx.channel().remoteAddress());

            // 获取到
            HttpRequest httpRequest = (HttpRequest) httpObject;
            // 获取uri, 过滤指定的资源
            URI uri = new URI(httpRequest.uri());
            if ("/favicon.ico".equals(uri.getPath())) {
                System.out.println("请求了 favicon.ico 不做响应");
                return;
            }

            // 回复信息给浏览器 【HTTP 协议】
            ByteBuf byteBuf = Unpooled.copiedBuffer("hello, 我是服务器，", CharsetUtil.UTF_8);
            // 构造一个HTTP 响应，既httpResponse
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf-8");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
            // 将构建好 response 返回
            ctx.writeAndFlush(response);
        }
    }
}
