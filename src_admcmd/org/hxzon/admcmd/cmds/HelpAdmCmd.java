package org.hxzon.admcmd.cmds;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

import org.hxzon.admcmd.AbstractAdmCmdImpl;
import org.hxzon.admcmd.AdmCmd;
import org.hxzon.admcmd.Gs;

public class HelpAdmCmd extends AbstractAdmCmdImpl implements AdmCmd {

    public HelpAdmCmd() {
        super("help");
    }

    @Override
    public void execute(String cmdLine, ChannelHandlerContext ctx) {
        String cmdName = cmdLine.trim();
        Map<String, AdmCmd> cmds = Gs.getAdmCmdHandler().getCmds();
        if (cmdName.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (String cmd : cmds.keySet()) {
                sb.append(cmd).append("  ");
                i++;
                if (i % 4 == 0) {
                    sb.append("\r\n");
                }
            }
            sb.append("\r\n");
            ctx.writeAndFlush(sb.toString());
        } else {
            AdmCmd admCmd = cmds.get(cmdName);
            if (admCmd == null) {
                ctx.writeAndFlush("can't find cmd\r\n");
                return;
            }
            ctx.writeAndFlush(admCmd.usage() + "\r\n");
        }
    }

}
