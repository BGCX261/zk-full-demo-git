package org.hxzon.admcmd;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

import org.hxzon.admcmd.cmds.DbAdmCmd;
import org.hxzon.admcmd.cmds.GroovyShellAdmCmd;
import org.hxzon.admcmd.cmds.HelpAdmCmd;
import org.hxzon.admcmd.cmds.LoginAdmCmd;
import org.hxzon.admcmd.cmds.WhoamiAdmCmd;
import org.hxzon.util.Data;

public class AdmCmdHandlerImpl implements AdmCmdHandler {
    private Map<String, AdmCmd> cmds = new HashMap<String, AdmCmd>();

    @Override
    public void addCmd(Class<? extends AdmCmd> cmdClazz) {
        AdmCmd cmd = Gs.bindReloadable(cmdClazz);
        cmds.put(cmd.getName(), cmd);
    }

    @Override
    public Map<String, AdmCmd> getCmds() {
        return cmds;
    }

    public AdmCmdHandlerImpl() {
        addCmds();
    }

    private void addCmds() {
        addCmd(LoginAdmCmd.class);
        addCmd(WhoamiAdmCmd.class);
        addCmd(HelpAdmCmd.class);
        addCmd(GroovyShellAdmCmd.class);
        addCmd(DbAdmCmd.class);
    }

    @Override
    public void handleConnect(ChannelHandlerContext ctx) {
        ctx.writeAndFlush("please login first\r\nlogin <accountname>\r\n>");
        Data data = Data.createData();
        Gs.getAdmClientManager().addChannel(ctx.channel(), data);
    }

    @Override
    public void handleDisconnect(ChannelHandlerContext ctx) {
        Gs.getAdmClientManager().removeChannel(ctx.channel());
    }

    @Override
    public void handle(String cmdLine, ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        boolean logined = false;
        Data data = Gs.getAdmClientManager().getChannelData(channel);
        AdmCmd curCmd = (AdmCmd) data.get(ChannelDataConsts.CurCmd);
        if (curCmd != null) {
            curCmd.executeContinue(cmdLine, ctx);
            printCmdStartTip(ctx, data);
            return;
        }
        logined = data.getBoolean(ChannelDataConsts.Logined, false);
        String[] tmp = cmdLine.split("[\\s+]", 2);
        String cmdName = tmp[0];
        String cmdArgs = tmp.length == 2 ? tmp[1] : null;
        AdmCmd admCmd = cmds.get(cmdName);
        if (admCmd == null) {
            ctx.writeAndFlush("can't find cmd\r\n>");
            return;
        }
        if (logined || !admCmd.needLogin()) {
            admCmd.execute(cmdArgs, ctx);
            printCmdStartTip(ctx, data);
        } else {
            ctx.writeAndFlush("please login first\r\n>");
        }
    }

    private void printCmdStartTip(ChannelHandlerContext ctx, Data data) {
        AdmCmd curCmd = (AdmCmd) data.get(ChannelDataConsts.CurCmd);
        if (curCmd == null) {
            ctx.writeAndFlush(">");
        } else {
            ctx.writeAndFlush(">>");
        }
    }

}
