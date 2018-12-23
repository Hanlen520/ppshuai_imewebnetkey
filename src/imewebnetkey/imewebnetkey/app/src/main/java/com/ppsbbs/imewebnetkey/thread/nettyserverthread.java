
package com.ppsbbs.imewebnetkey.thread;

import com.ppsbbs.imewebnetkey.callback.*;
import com.ppsbbs.imewebnetkey.network.server.*;
import java.lang.Thread;
import java.lang.InterruptedException;

public class nettyserverthread extends Thread {
	private volatile int port = 8080;
    private volatile nettyserver server = null;

	public nettyserverthread(int port, taskcallback taskCallBack)
	{
		this.port = port;
		server = new nettyserver(taskCallBack);
	}
    public void stop_task(){
		if(server!=null){
            server.stop();
            System.out.println("close server successed");
        }
    }
    @Override
    public void run() {
		while(!Thread.currentThread().isInterrupted()) {			
			try {
				server.start(this.port);
			} catch (Exception e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}		
		}
    }
    public static void main(String[] args) {
        nettyserverthread task = new nettyserverthread(8080, null);
        try {
			task.start();
			Thread.sleep(1000);
			task.stop_task();
        } catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
    }
}