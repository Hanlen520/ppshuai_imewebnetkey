
package com.ppsbbs.imewebnetkey.handler;

import com.ppsbbs.imewebnetkey.callback.*;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class childchannelhandler extends ChannelInitializer<SocketChannel> {
	private taskcallback taskCallBack = null;
	public childchannelhandler(taskcallback taskCallBack)
	{
		this.taskCallBack = taskCallBack;
	}
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new nettyserverhandler(this.taskCallBack));
    }
}