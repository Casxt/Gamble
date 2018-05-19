
import Request.Request;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class Acceptor implements CompletionHandler<AsynchronousSocketChannel, Request> {
    private LinkedBlockingQueue<Request> ReqQueue;
    private AsynchronousServerSocketChannel Server;
    private static Logger log = Logger.getLogger(Acceptor.class.getName());

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

        if (e instanceof java.nio.channels.ClosedChannelException) {
            log.severe("Server Stoped!");
            e.getStackTrace();
        } else {
            log.severe(e.toString());
            Server.accept(new Request(ReqQueue), this);
        }
    }

}