import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Request {
    AsynchronousSocketChannel ch;
    private LinkedBlockingQueue<Request> ReqQueue;
    Reader reader;
    public Request(LinkedBlockingQueue<Request> ReqQueue){
        this.ReqQueue = ReqQueue;
        ch = null;
        reader = null;
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

    public void Close(){
        try {
            ch.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
