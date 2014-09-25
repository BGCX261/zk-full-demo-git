package org.hxzon.admcmd;

import io.netty.channel.ChannelHandlerContext;

public interface AdmCmd {

    String getName();

    void execute(String cmdLine, ChannelHandlerContext ctx);

    void executeContinue(String cmdLine, ChannelHandlerContext ctx);

    String usage();

    boolean needLogin();
}
