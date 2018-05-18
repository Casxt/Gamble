import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

public class Request {
    AsynchronousSocketChannel ch;
    private LinkedBlockingQueue<Request> ReqQueue;

    public Request(LinkedBlockingQueue<Request> ReqQueue){
        this.ReqQueue = ReqQueue;
    }

    /**
     * call GetReq Will Start to collect data until complete
     * @param ch
     */
    public void GetReq(AsynchronousSocketChannel ch){
        this.ch = ch;
    }
}
