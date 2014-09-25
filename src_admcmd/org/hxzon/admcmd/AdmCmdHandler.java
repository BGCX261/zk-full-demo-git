package org.hxzon.admcmd;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

public interface AdmCmdHandler {

    void handle(String cmdLine, ChannelHandlerContext ctx);

    void addCmd(Class<? extends AdmCmd> cmdClazz);

    Map<String, AdmCmd> getCmds();

    void handleConnect(ChannelHandlerContext ctx);

    void handleDisconnect(ChannelHandlerContext ctx);
}
