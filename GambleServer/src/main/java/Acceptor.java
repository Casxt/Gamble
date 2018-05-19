import Request.Request;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.LinkedBlockingQueue;

public class Acceptor implements CompletionHandler<AsynchronousSocketChannel, Request> {
    private LinkedBlockingQueue<Request> ReqQueue;
    private AsynchronousServerSocketChannel Server;

    Acceptor(AsynchronousServerSocketChannel Server, LinkedBlockingQueue<Request> ReqQueue) {
        this.ReqQueue = ReqQueue;
        this.Server = Server;
    }

    @Override
    public void completed(AsynchronousSocketChannel ch, Request req) {
        req.GetReq(ch);
        Server.accept(new Request(ReqQueue), this);
    }

    @Override
    public void failed(Throwable e, Request req) {
        Server.accept(new Request(ReqQueue), this);
        e.getStackTrace();
    }
}