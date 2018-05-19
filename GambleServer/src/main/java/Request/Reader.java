package Request;

import PackTool.PackTool;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

public class Reader implements CompletionHandler<Integer, AsynchronousSocketChannel> {
    private Request req;
    ByteBuffer Buff;
    private PackTool depacker;
    private int readTimes;
    public Reader(Request req) {
        this.req = req;
        Buff = ByteBuffer.allocate(2048);
        depacker = new PackTool(new byte[] {'G','r','a','m','b','l','e'});
        readTimes = 0;
    }

    @Override
    public void completed(Integer result, AsynchronousSocketChannel ch) {
        readTimes++;
        if (result != -1){
            Buff.flip();
            byte[] data = depacker.DataDeconstructor(Buff);
            Buff.compact();
            if(data != null){

                req.DataReadComplete(data);

            } else {// if data incomplete, read more
                if(readTimes < 4) {//if read too many times
                    ch.read(Buff, 10, TimeUnit.SECONDS, ch, this);
                } else {
                    req.Close();
                }
            }

        } else {
            req.Close();
        }
    }


    @Override
    public void failed(Throwable e, AsynchronousSocketChannel ch) {
        // if Client.Client Closed, may cause this err
        req.Close();
        e.printStackTrace();
    }

    /**
     * Reset buffer and read counter
     */
    public void Reset(){
        Buff.clear();
        readTimes = 0;
    }
}
