package Request;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Request.Request will format a socket data into request
 */
public class Request {
    public AsynchronousSocketChannel ch;
    private LinkedBlockingQueue<Request> ReqQueue;
    private RequestReader reader;
    private RequestWriter writer;
    public JSONObject body;

    public Request(LinkedBlockingQueue<Request> ReqQueue){
        this.ReqQueue = ReqQueue;
        ch = null;
        reader = new RequestReader(this);
        body = null;
        writer = new RequestWriter(this);
    }

    public void Response(JSONObject res){
        writer.WriteOnce(res.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
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
        // Set Socket
        this.ch = ch;
        // Set callback, after this, the reader will handle
        // until the ch until a complete req are recevie
        this.ch.read(reader.Buff, 60, TimeUnit.SECONDS, this.ch,reader);
    }

    /**
     * call KeepOpen, the socket will not close after response
     * can use to reuse connection
     */
    public void KeepOpen(){
        writer.keepOpen = true;
    }

    /**
     * call CloseAfterSend, the socket will close after response
     */
    public void CloseAfterSend(){
        writer.keepOpen = false;
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
