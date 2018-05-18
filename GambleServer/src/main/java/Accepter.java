import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.LinkedBlockingQueue;

public class Accepter  implements CompletionHandler <AsynchronousSocketChannel, Request> {
    //LinkedBlockingQueue<Request> ReqQueue;

    @Override
    public void completed(AsynchronousSocketChannel ch, Request req) {
        req.GetReq(ch);
    }

    @Override
    public void failed(Throwable exc, Request req) {

    }
}