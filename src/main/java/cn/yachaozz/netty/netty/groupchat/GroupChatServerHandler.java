package cn.yachaozz.netty.netty.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author HouYC
 * @create 2020-10-26-21:14
 */
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {


    /**
     * 定义一个channel 组，管理所有的channel
     * GlobalEventExecutor.INSTANCE 是全局的事件执行器，是一个单例
     */
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * handlerAdded 表示连接建立，一旦建立，第一个被执行
     * 将当前channel 加入到channelGroup
     * @param ctx
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        // 将该客户加入聊天的信息推送给其他在线的客户端...  该方法会将 channelGroup 中所有的channel遍历，并发送消息，我们不需要自己遍历
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 加入聊天" + sf.format(new Date()) + "\n");
        channelGroup.add(channel);
    }

    /**
     * 断开链接， 将xx 客户离开信息推送给当前在线的客户
     * @param ctx
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {

        Channel channel = ctx.channel();

        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 离开了 \n");
        System.out.println("channelGroup.size() = " + channelGroup.size());
    }

    /**
     * 表示channel 处于不活动状态，提示 xx 离线了
     * @param ctx
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + " 离线了~~");
    }

    /**
     *  读取数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // 获取当前channel
        Channel channel = ctx.channel();
        // 这时我们遍历channelGroup，根据不同的情况，会送不同的消息
        channelGroup.forEach(ch -> {
            if (channel != ch) {
                // 不是当前的channel，转发消息
                ch.writeAndFlush("[客户]" + channel.remoteAddress() + "发送了消息" + msg + "\n");
            } else {
                // 回显自己发送的消息给自己
                ch.writeAndFlush("[自己]发送了消息" + msg + "\n");
            }
        });
    }

    /**
     * 异常处理
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 关闭通道
        ctx.close();
    }
}
