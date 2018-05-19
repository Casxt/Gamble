package Client;

import PackTool.PackTool;
import MsgHandle.MsgHandle;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

public class Reader implements CompletionHandler<Integer, AsynchronousSocketChannel> {
    private Client client;
    public ByteBuffer Buff;
    private PackTool depacker;

    private int readTimes = 0;

    public Reader(Client client) {
        this.client = client;
        Buff = ByteBuffer.allocate(2048);
        depacker = new PackTool(new byte[] {'G','r','a','m','b','l','e'});
    }

    @Override
    public void completed(Integer result, AsynchronousSocketChannel ch) {
        readTimes++;
        if (result != -1){
            Buff.flip();

            byte[] data = depacker.DataDeconstructor(Buff);

            Buff.compact();
            if(data != null){

                MsgHandle.Parse(data);
                //开始接受下次消息
                readTimes = 0;
                ch.read(Buff, 20, TimeUnit.SECONDS, ch, this);
            } else {// if data incomplete, read more
                if(readTimes < 4) {//if read too many times
                    //因为一直在开局，所以应该不会长时间无消息
                    ch.read(Buff, 20, TimeUnit.SECONDS, ch, this);
                } else {
                    client.Quite();
                }
            }

        } else {
            // TODO:Info Something
            client.Quite();
        }
    }


    @Override
    public void failed(Throwable e, AsynchronousSocketChannel ch) {
        // if Client.Client Closed, may cause this err
        client.Quite();
        e.printStackTrace();
    }
}
