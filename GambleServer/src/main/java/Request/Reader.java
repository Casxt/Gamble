package Request;

import PackTool.PackTool;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

public class Reader implements CompletionHandler<Integer, AsynchronousSocketChannel> {
    private Request req;
    ByteBuffer Buff = ByteBuffer.allocate(2048);
    private PackTool depacker = new PackTool(new byte[]{'G', 'r', 'a', 'm', 'b', 'l', 'e'});
    private int readTimes = 0;

    Reader(Request req) {
        this.req = req;
    }

    @Override
    public void completed(Integer result, AsynchronousSocketChannel ch) {
        readTimes++;
        if (result != -1) {
            Buff.flip();
            byte[] data = depacker.DataDeconstructor(Buff);
            Buff.compact();
            if (data != null) {

                req.DataReadComplete(data);

            } else {// if data incomplete, read more
                if (readTimes < 4) {//if read too many times
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
        if (e instanceof java.nio.channels.InterruptedByTimeoutException) {
            req.Close();
        } else {
            req.Close();
            e.printStackTrace();
        }
    }

    /**
     * Reset buffer and read counter
     */
    void Reset() {
        Buff.clear();
        readTimes = 0;
    }
}
