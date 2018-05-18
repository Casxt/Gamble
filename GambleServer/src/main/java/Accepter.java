import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class Accepter  implements CompletionHandler <AsynchronousSocketChannel, Request> {

    @Override
    public void completed(AsynchronousSocketChannel ch, Request req) {
        req.GetReq(ch);
    }

    @Override
    public void failed(Throwable e, Request req) {
        e.getStackTrace();
    }
}