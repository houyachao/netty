package cn.yachaozz.netty.netty.groupchat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author HouYC
 * @create 2020-10-26-22:38
 */
public class MyServerHandle extends ChannelInboundHandlerAdapter {

    /**
     *
     * @param ctx  上下文
     * @param evt  事件
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {

        if (evt instanceof IdleStateEvent) {
            // 将 Evt 向下转型 IdleStateEvent
            IdleStateEvent event = (IdleStateEvent)evt;
            String eventType = null;
            switch (event.state()) {
                case ALL_IDLE:
                    eventType = "读写空闲";
                    break;
                case READER_IDLE:
                    eventType = "读空闲";
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    break;
            }
            System.out.println(ctx.channel().remoteAddress() + "---超时时间--" + eventType);
            System.out.println("服务器做相应处理");

        }
    }
}
