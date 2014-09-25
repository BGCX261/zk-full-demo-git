package org.hxzon.admcmd.service.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicBoolean;

import org.hxzon.admcmd.AdmAccount;
import org.hxzon.admcmd.AdmClientManager;
import org.hxzon.admcmd.AdmClientManagerImpl;
import org.hxzon.admcmd.AdmCmdHandler;
import org.hxzon.admcmd.AdmCmdHandlerImpl;
import org.hxzon.admcmd.AdminServerHandler;
import org.hxzon.admcmd.Gs;
import org.hxzon.admcmd.service.AdmCmdService;
import org.springframework.stereotype.Service;

@Service
public class AdmCmdServiceImpl implements AdmCmdService {

    public static final String encoding = "UTF-8";
    public static final Charset charset = Charset.forName(encoding);
    public static final int adminServerPort = 9999;
    public static final int MaxFrameLength = 10240;//10k
    public static final int LengthFieldLength = 4;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture server;
    private AtomicBoolean started = new AtomicBoolean();

    public AdmCmdServiceImpl() {
        start();
    }

    @Override
    public void start() {
        if (started.get()) {
            return;
        }
        started.set(true);
        //
        Gs.bindReloadable(AdmClientManager.class, AdmClientManagerImpl.class);
        Gs.bindReloadable(AdmCmdHandler.class, AdmCmdHandlerImpl.class);
        AdmClientManager admManager = Gs.getAdmClientManager();
        admManager.addAdmAccount(new AdmAccount("adm", "adm"));
        admManager.addAdmAccount(new AdmAccount("test", "test"));
        //
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new StringDecoder(charset));
                    ch.pipeline().addLast(new StringEncoder(charset));
                    ch.pipeline().addLast(new AdminServerHandler());
                }
            }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            server = b.bind(adminServerPort).sync();

        } catch (Exception e) {

        } finally {

        }
    }

    @Override
    public void stop() {
        if (!started.get()) {
            return;
        }
        started.set(false);
        //
        try {
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            server.channel().closeFuture().sync();
        } catch (Exception e) {

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
