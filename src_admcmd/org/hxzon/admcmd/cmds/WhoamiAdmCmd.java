package org.hxzon.admcmd.cmds;

import io.netty.channel.ChannelHandlerContext;

import org.hxzon.admcmd.AbstractAdmCmdImpl;
import org.hxzon.admcmd.AdmAccount;
import org.hxzon.admcmd.AdmCmd;
import org.hxzon.admcmd.ChannelDataConsts;
import org.hxzon.admcmd.Gs;

public class WhoamiAdmCmd extends AbstractAdmCmdImpl implements AdmCmd {

    public WhoamiAdmCmd() {
        super("whoami");
    }

    @Override
    public void execute(String cmdLine, ChannelHandlerContext ctx) {
        AdmAccount admAccount = (AdmAccount) Gs.getAdmClientManager().getChannelData(ctx.channel()).get(ChannelDataConsts.AdmAccount);
        ctx.writeAndFlush(admAccount.getName() + "\r\n");
    }

}
