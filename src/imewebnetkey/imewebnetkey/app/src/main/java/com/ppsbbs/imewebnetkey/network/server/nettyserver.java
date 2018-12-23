
package com.ppsbbs.imewebnetkey.network.server;

import com.ppsbbs.imewebnetkey.callback.*;
import com.ppsbbs.imewebnetkey.handler.*;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class nettyserver {
    private static final int PORT = 8080;
	private taskcallback taskCallBack = null;
	private EventLoopGroup bossGroup = null;
    private EventLoopGroup workerGroup = null;
    private ServerBootstrap serverBootstrap = null;
	public nettyserver(taskcallback taskCallBack)
	{
		this.taskCallBack = taskCallBack;
	}
    public void start(int port) {
        //Configure NIO thread group
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        //Create ServerBootstrap
        serverBootstrap = new ServerBootstrap();
        //Bind group on channel handler
        serverBootstrap.group(bossGroup, workerGroup).
                channel(NioServerSocketChannel.class).
                option(ChannelOption.SO_BACKLOG, 1024).
                childHandler(new childchannelhandler(this.taskCallBack));
        try {
            //Bind port synchronous wait for success
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            //synchronous wait server listen port closed
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
			if(bossGroup != null)
			{
				bossGroup.shutdownGracefully();
			}
			if(workerGroup != null)
			{
				workerGroup.shutdownGracefully();
			}
			if(serverBootstrap != null)
			{
				//serverBootstrap.releaseExternalResources();
			}
        }
    }

	public void stop()
	{
		// Close the serverChannel and then all accepted connections.
		if(bossGroup != null)
		{
			bossGroup.shutdownGracefully();
		}
		if(workerGroup != null)
		{
			workerGroup.shutdownGracefully();
		}
		if(serverBootstrap != null)
		{
			//serverBootstrap.releaseExternalResources();
		}
	}
	
    public static void main(String[] args) {
        new nettyserver(null).start(PORT);
    }
}