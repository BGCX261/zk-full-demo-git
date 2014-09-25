package org.hxzon.admcmd.cmds;

import io.netty.channel.ChannelHandlerContext;

import org.hxzon.admcmd.AbstractAdmCmdImpl;
import org.hxzon.admcmd.AdmAccount;
import org.hxzon.admcmd.AdmClientManager;
import org.hxzon.admcmd.AdmCmd;
import org.hxzon.admcmd.ChannelDataConsts;
import org.hxzon.admcmd.Gs;
import org.hxzon.util.Data;

public class LoginAdmCmd extends AbstractAdmCmdImpl implements AdmCmd {

    public LoginAdmCmd() {
        super("login");
    }

    @Override
    public void execute(String cmdLine, ChannelHandlerContext ctx) {
        String admName = cmdLine.trim();
        AdmClientManager admManager = Gs.getAdmClientManager();
        AdmAccount admAccount = admManager.findAdmAccount(admName);
        Data data = admManager.getChannelData(ctx.channel());
        if (admAccount != null) {
            data.put(ChannelDataConsts.AdmAccount, admAccount);
            data.put(ChannelDataConsts.CurCmd, this);
            ctx.writeAndFlush("password:");
        } else {
            data.remove(ChannelDataConsts.Logined);
            ctx.writeAndFlush("can't find admAccount\r\n");
        }

    }

    @Override
    public void executeContinue(String cmdLine, ChannelHandlerContext ctx) {
        String password = cmdLine.trim();
        AdmClientManager admManager = Gs.getAdmClientManager();
        Data data = admManager.getChannelData(ctx.channel());
        data.remove(ChannelDataConsts.CurCmd);
        AdmAccount admAccount = (AdmAccount) data.get(ChannelDataConsts.AdmAccount);
        if (admAccount.getPassword().equals(password)) {
            data.put(ChannelDataConsts.Logined, true);
            ctx.writeAndFlush("logined\r\n");
        } else {
            //admManager.removeChannel(ctx.channel());
            ctx.writeAndFlush("password error\r\n");
        }
    }

    @Override
    public String usage() {
        return getName() + "  <accountname>";
    }

    @Override
    public boolean needLogin() {
        return false;
    }
}
