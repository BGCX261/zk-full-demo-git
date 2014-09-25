package org.hxzon.admcmd.cmds;

import groovy.lang.GroovyShell;
import io.netty.channel.ChannelHandlerContext;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.hxzon.admcmd.AbstractAdmCmdImpl;
import org.hxzon.admcmd.AdmClientManager;
import org.hxzon.admcmd.AdmCmd;
import org.hxzon.admcmd.ChannelDataConsts;
import org.hxzon.admcmd.GroovyShellUtil;
import org.hxzon.admcmd.Gs;
import org.hxzon.util.Data;

public class GroovyShellAdmCmd extends AbstractAdmCmdImpl implements AdmCmd {

    private static final String Inputs = "GroovyShell.inputs";
    private static final String GroovyShell = "GroovyShell.shell";

    public GroovyShellAdmCmd() {
        super("shell");
    }

    @Override
    public void execute(String cmdLine, ChannelHandlerContext ctx) {
        AdmClientManager admManager = Gs.getAdmClientManager();
        Data data = admManager.getChannelData(ctx.channel());
        data.put(ChannelDataConsts.CurCmd, this);
        data.put(Inputs, new StringBuilder());
        data.put(GroovyShell, GroovyShellUtil.getShell());
    }

    @Override
    public void executeContinue(String cmdLine, ChannelHandlerContext ctx) {
        AdmClientManager admManager = Gs.getAdmClientManager();
        Data data = admManager.getChannelData(ctx.channel());
        StringBuilder inputs = (StringBuilder) data.get(Inputs);
        if ("-q\r\n".equals(cmdLine)) {
            data.remove(Inputs);
            data.remove(ChannelDataConsts.CurCmd);
            GroovyShell shell = (GroovyShell) data.remove(GroovyShell);
            GroovyShellUtil.returnShell(shell);
        } else if ("-e\r\n".equals(cmdLine)) {
            String outputs = executeGroovyScript(inputs.toString(), data);
            ctx.writeAndFlush(outputs + "\r\n");
            data.put(Inputs, new StringBuilder());
        } else if ("-c\r\n".equals(cmdLine)) {
            data.put(Inputs, new StringBuilder());
        } else {
            inputs.append(cmdLine);
        }
    }

    @Override
    public String usage() {
        return "-q quit\r\n"//
                + "-c clear inputs\r\n"//
                + "-e execute\r\n"//
                + "use out for print";
    }

    //=================
    private static final String preImport = new StringBuilder()//
            .append("import org.hxzon.admcmd.Gs;\r\n")//
            .append("import org.hxzon.project.*;\r\n")//
            .toString();

    public static String executeGroovyScript(String inputs, Data data) {
        StringBuilder outputs = new StringBuilder();
        try {
            StringWriter sw = new StringWriter();
            PrintWriter out = new PrintWriter(sw);
            GroovyShell shell = (GroovyShell) data.get(GroovyShell);
            shell.setVariable("out", out);
            Object result = shell.evaluate(preImport + inputs);
            outputs.append(sw.toString());
            if (result != null) {
                outputs.append(result.toString());
            } else {
                outputs.append("no result");
            }
        } catch (Exception e) {
            outputs.append(e.getMessage());
        }
        return outputs.toString();
    }
}
