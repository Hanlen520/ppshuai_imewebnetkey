
package com.ppsbbs.imewebnetkey.network.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
 
/**
 * Created by xingyun86 on 2018/12/23.
 */
public class nioserver {
 
    public static Map<Socket,Long> geym_time_stat=new HashMap<Socket,Long>(10240);
    class echoclient{
        private LinkedList<ByteBuffer> outq;
        echoclient(){
            outq=new LinkedList<ByteBuffer>();
        }
        //return the output queue
        public LinkedList<ByteBuffer> getOutputQueue(){
            return outq;
        }
        //enqueue a ByteBuffer on the output queue.
        public void enqueue(ByteBuffer bb){
            outq.addFirst(bb);
        }
    } 
 
    class messagehandler implements Runnable{
        SelectionKey sk;
        ByteBuffer bb;
 
        public messagehandler(SelectionKey sk, ByteBuffer bb) {
            this.sk = sk;
            this.bb = bb;
        }
 
        @Override
        public void run() {
            echoclient echoClient=(echoclient)sk.attachment();
            echoClient.enqueue(bb);
 
            //we've enqueued data to be written to the client,we must
            //not set interest in OP_WRITE
            sk.interestOps(SelectionKey.OP_READ|SelectionKey.OP_WRITE);
            abstractSelector.wakeup();
        }
    }
 
    private Selector abstractSelector;
    private ExecutorService excutorService = Executors.newCachedThreadPool();
 
    /*
      Accept a new client and set it up for reading
     */
    private void doAccept(SelectionKey sk){
        ServerSocketChannel server=(ServerSocketChannel)sk.channel();
        SocketChannel clientChannel;
        try {
            //Get client channel
            clientChannel = server.accept();
            clientChannel.configureBlocking(false);
 
            //Register the channel for reading
            SelectionKey clientKey=clientChannel.register(abstractSelector,SelectionKey.OP_READ);
            //Allocate an EchoClient instance and attach it to this selection key.
            echoclient echoClient=new echoclient();
            clientKey.attach(echoClient);
 
            InetAddress clientAddress=clientChannel.socket().getInetAddress();
            System.out.println("Accepted connetion from "+clientAddress.getHostAddress()+".");
        }catch (Exception e){
            System.out.println("Failed to accept new client");
            e.printStackTrace();
        }
    }
 
    private void doRead(SelectionKey sk){
        SocketChannel channel=(SocketChannel)sk.channel();
        ByteBuffer bb=ByteBuffer.allocate(8192);
        int len;
 
        try {
            len=channel.read(bb);
            if(len<0){
                disconnect(sk);
                return;
            }
        }catch (Exception e){
            System.out.println("Fail to read from client");
            e.printStackTrace();
            disconnect(sk);
            return;
        }
        bb.flip();
        excutorService.execute(new messagehandler(sk,bb));
    }
 
    private void doWrite(SelectionKey sk){
        SocketChannel channel=(SocketChannel)sk.channel();
        echoclient echoClient=(echoclient)sk.attachment();
        LinkedList<ByteBuffer> outq=echoClient.getOutputQueue();
 
        ByteBuffer bb=outq.getLast();
        try {
            int len=channel.write(bb);
            if(len==-1){
                disconnect(sk);
                return;
            }
            if(bb.remaining()==0){
                outq.removeLast();
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("fail to write to client");
            disconnect(sk);
        }
 
        if(outq.size()==0){
            sk.interestOps(SelectionKey.OP_READ);
        }
 
    }
    private void disconnect(SelectionKey sk){
        SocketChannel sc=(SocketChannel)sk.channel();
        try {
            sc.finishConnect();
        }catch (IOException e){
 
        }
    }
  
    private void startServer(int nPort) throws Exception{
        //define a selector
        abstractSelector= SelectorProvider.provider().openSelector();
 
        //define a server socket channel
        ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
		//set the server socket channel is non blocking
        serverSocketChannel.configureBlocking(false);
		
		//InetSocketAddress inetSocketAddress=new InetSocketAddress(InetAddress.getLocalHost(),8000);
        //define server port
        InetSocketAddress inetSocketAddress=new InetSocketAddress(nPort);
        //server socket channel bind to the port
        serverSocketChannel.socket().bind(inetSocketAddress);
        //Register socketchannel to a selector, and select events of listen, SelectionKey.OP_ACCEPT is: 
        //	if selector listen server socket channel of register on it prepared accept a connect or a error of hang-up,
        //	selector add OP_ACCEPT to key ready set and add key to selected-key set.
        SelectionKey selectedKeyAccept=serverSocketChannel.register(abstractSelector,SelectionKey.OP_ACCEPT);
 
        while (true) {
            abstractSelector.select();
            Set<SelectionKey> selectedKeys=abstractSelector.selectedKeys();
            Iterator<SelectionKey> i=selectedKeys.iterator();
            long e=0;
            while (i.hasNext()){
                SelectionKey sk=(SelectionKey)i.next();
                i.remove();
 
                if(sk.isAcceptable()){
                    doAccept(sk);
                }else if(sk.isValid()&&sk.isReadable()){
                    if(!geym_time_stat.containsKey(((SocketChannel)sk.channel()).socket())){
                        geym_time_stat.put(((SocketChannel)sk.channel()).socket(),System.currentTimeMillis());
                        doRead(sk);
                    }
                }else if(sk.isValid()&&sk.isWritable()){
                    doWrite(sk);
                    e=System.currentTimeMillis();
                    long b=geym_time_stat.remove(((SocketChannel)sk.channel()).socket());
                    System.out.println("spend"+(e-b)+"ms");
                }
            }
        }
    }
 
    public static void main(String[] args) {
        nioserver echoServer=new nioserver();
        try {
            echoServer.startServer(8080);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}