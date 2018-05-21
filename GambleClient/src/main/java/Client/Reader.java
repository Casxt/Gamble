package Client;

import MsgHandle.MsgHandle;
import PackTool.PackTool;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Reader implements CompletionHandler<Integer, AsynchronousSocketChannel> {
    private static Logger log = Logger.getLogger(Reader.class.getName());
    ByteBuffer Buff = ByteBuffer.allocate(2048);
    private Client client;
    private PackTool depacker = new PackTool(new byte[]{'G', 'r', 'a', 'm', 'b', 'l', 'e'});
    private MsgHandle msgHandle;

    private int readTimes = 0;

    Reader(Client client) {
        this.client = client;
        msgHandle = new MsgHandle(client);
    }

    @Override
    public void completed(Integer result, AsynchronousSocketChannel ch) {
        readTimes++;
        if (result != -1) {
            Buff.flip();

            byte[] data = depacker.Deconstruct(Buff);

            Buff.compact();
            if (data != null) {

                msgHandle.Parse(data);
                //开始接受下次消息
                readTimes = 0;
                ch.read(Buff, 35, TimeUnit.SECONDS, ch, this);
            } else {// if data incomplete, read more
                if (readTimes < 4) {//if read too many times
                    //因为一直在开局，所以应该不会长时间无消息
                    ch.read(Buff, 35, TimeUnit.SECONDS, ch, this);
                } else {
                    client.Quite();
                }
            }

        } else {
            log.info("Client read -1 and quite");
            client.Quite();
        }
    }


    @Override
    public void failed(Throwable e, AsynchronousSocketChannel ch) {
        // if Client.Client Closed, may cause this err
        // if timeout
        if (e instanceof java.nio.channels.InterruptedByTimeoutException) {
            System.out.println("接收超时，断开与服务器的连接");
        } else if (e instanceof java.io.IOException){
            //java.io.IOException: 远程主机强迫关闭了一个现有的连接。
            log.info("server remote closed");
        } else {
            e.printStackTrace();
        }
        client.Quite();
    }
}
