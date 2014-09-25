package org.hxzon.admcmd;

import io.netty.channel.ChannelHandlerContext;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAdmCmdImpl implements AdmCmd {

    private static final Logger logger = LoggerFactory.getLogger(AbstractAdmCmdImpl.class);
    private String name;
    private Options options;
    private final CommandLineParser parser;

    public AbstractAdmCmdImpl(String name) {
        this.name = name;
        this.options = buildOptions();
        this.parser = new GnuParser();
    }

    protected Options buildOptions() {
        return null;
    }

    @Override
    public abstract void execute(String cmdLine, ChannelHandlerContext ctx);

    @Override
    public void executeContinue(String cmdLine, ChannelHandlerContext ctx) {
    }

    protected CommandLine parseCmd(String cmdLine, ChannelHandlerContext ctx) {
        try {
            return parser.parse(options, cmdLine.split("[\\s+]"));
        } catch (ParseException exp) {
            logger.error("Parsing adm cmd failed.  Reason: " + exp.getMessage());
            ctx.writeAndFlush(exp.getMessage());
        }
        return null;
    }

    @Override
    public String usage() {
        if (options != null) {
            StringBuilder sb = new StringBuilder(name);
            sb.append("\r\n");
            for (Object option : options.getOptions()) {
                Option opt = (Option) option;
                sb.append(opt.getOpt());
                if (opt.hasArg() && opt.hasArgName()) {
                    sb.append("  <").append(opt.getArgName()).append(">");
                }
                sb.append("\t\t").append(opt.getDescription()).append("\r\n");
            }
            return sb.toString();
        }
        return "no options";
    }

    @Override
    public boolean needLogin() {
        return true;
    }

    //
    @Override
    public String getName() {
        return name;
    }

    protected Options getOptions() {
        return options;
    }
}
