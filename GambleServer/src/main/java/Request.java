import org.json.JSONObject;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Request will format a socket data into request
 */
public class Request {
    AsynchronousSocketChannel ch;
    private LinkedBlockingQueue<Request> ReqQueue;
    Reader reader;
    JSONObject body;

    public Request(LinkedBlockingQueue<Request> ReqQueue){
        this.ReqQueue = ReqQueue;
        ch = null;
        reader = null;
        body = null;
    }

    /**
     * When reader read complete it will call this function
     * @param data is the reader return back
     */
    public void DataReadComplete(byte[] data){
        String s = new String(data, java.nio.charset.StandardCharsets.UTF_8);
        body = new JSONObject(s);
        // offer is a none block method,
        // put is a block method
        ReqQueue.offer(this);
        reader.Reset();
    }

    /**
     * call GetReq Will Start to collect data until complete
     * @param ch are socket bound to this
     */
    public void GetReq(AsynchronousSocketChannel ch){
        // Set Reader
        reader = new Reader(this);
        // Set Socket
        this.ch = ch;
        // Set callback, after this, the reader will handle
        // until the ch until a complete req are recevie
        this.ch.read(reader.Buff, 10, TimeUnit.SECONDS, this.ch,reader);
    }

    /**
     * Close the channel
     */
    public void Close(){
        try {
            ch.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
