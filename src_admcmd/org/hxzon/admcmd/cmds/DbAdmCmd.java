package org.hxzon.admcmd.cmds;

import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import org.hxzon.admcmd.AbstractAdmCmdImpl;
import org.hxzon.admcmd.AdmClientManager;
import org.hxzon.admcmd.AdmCmd;
import org.hxzon.admcmd.ChannelDataConsts;
import org.hxzon.admcmd.Gs;
import org.hxzon.util.Data;
import org.hxzon.util.db.DataSet;
import org.hxzon.util.db.Db;

public class DbAdmCmd extends AbstractAdmCmdImpl implements AdmCmd {

    private static final String Inputs = "Db.inputs";
    private static final String Db = "Db.db";

    public DbAdmCmd() {
        super("db");
    }

    @Override
    public void execute(String cmdLine, ChannelHandlerContext ctx) {
        AdmClientManager admManager = Gs.getAdmClientManager();
        Data data = admManager.getChannelData(ctx.channel());
        data.put(ChannelDataConsts.CurCmd, this);
        data.put(Inputs, new StringBuilder());
    }

    @Override
    public void executeContinue(String cmdLine, ChannelHandlerContext ctx) {
        AdmClientManager admManager = Gs.getAdmClientManager();
        Data data = admManager.getChannelData(ctx.channel());
        StringBuilder inputs = (StringBuilder) data.get(Inputs);
        if ("-q\r\n".equals(cmdLine)) {
            data.remove(Inputs);
            data.remove(Db);
            data.remove(ChannelDataConsts.CurCmd);
        } else if ("-e\r\n".equals(cmdLine)) {
            executeSql(data, ctx);
        } else if (cmdLine.startsWith("-db")) {
            db(cmdLine, data, ctx);
        } else {
            inputs.append(cmdLine);
        }
    }

    private void db(String cmdLine, Data data, ChannelHandlerContext ctx) {
        String[] tmp = cmdLine.trim().split("[\\s+]", 2);
        if (tmp.length == 1) {
            Db db = (Db) data.get(Db);
            ctx.writeAndFlush(db == null //
            ? "had not select db\r\n" //
                    : "cur db is " + db.getName() + "\r\n");
        } else if (tmp.length == 2) {
            Db db = Gs.getDb(tmp[1]);
            if (db == null) {
                ctx.writeAndFlush("can't find db\r\n");
            } else {
                data.put(Db, db);
                ctx.writeAndFlush("cur db is " + db.getName() + "\r\n");
            }
        }
    }

    private void executeSql(Data data, ChannelHandlerContext ctx) {
        Db db = (Db) data.get(Db);
        if (db == null) {
            ctx.writeAndFlush("please select db first\r\n");
            return;
        }
        String sql = ((StringBuilder) data.get(Inputs)).toString();
        try {
            DataSet result = db.execute(sql);
            printResult(result, ctx);
        } catch (Exception e) {
            ctx.writeAndFlush(e.getMessage() + "\r\n");
        }
        data.put(Inputs, new StringBuilder());
    }

    private void printResult(DataSet dataset, ChannelHandlerContext ctx) throws Exception {
        if (dataset.isUpdate()) {
            ctx.writeAndFlush("update rows " + dataset.getRowNums() + "\r\n\r\n");
            return;
        }
        ctx.writeAndFlush("result rows " + dataset.getRowNums() + "\r\n\r\n");
        for (String columnName : dataset.getColumnNames()) {
            ctx.writeAndFlush(columnName + "\t");
        }
        ctx.writeAndFlush("\r\n\r\n");
        for (List<Object> row : dataset.getResults()) {
            for (Object cell : row) {
                ctx.writeAndFlush(cell + "\t");
            }
            ctx.writeAndFlush("\r\n\r\n");
        }
    }

    @Override
    public String usage() {
        return "-q quit\r\n" //
                + "-e execute query or update\r\n"//
                + "-db show dbName\r\n" //
                + "-db <dbName> select db\r\n";
    }
}
