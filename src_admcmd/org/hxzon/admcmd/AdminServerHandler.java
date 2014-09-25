package org.hxzon.admcmd;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminServerHandler extends ChannelHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(AdminServerHandler.class);

    public AdminServerHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Gs.getAdmCmdHandler().handleConnect(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Gs.getAdmCmdHandler().handleDisconnect(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String cmdLine = (String) msg;
        Gs.getAdmCmdHandler().handle(cmdLine, ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        logger.debug("", e.getCause());
        //ctx.close();
    }

//    private String getIp(SocketAddress addr) {
//        String remoteIp = addr.toString();
//        int index = remoteIp.indexOf(':');
//        return remoteIp.substring(1, index);
//    }
}
