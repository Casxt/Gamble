import Client.Client;
import Request.Request;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionRelay {
    AsynchronousChannelGroup group;
    AsynchronousServerSocketChannel Server;
    // LinkedBlockingQueue are Thread safe, see:
    // https://stackoverflow.com/questions/2695426/are-linkedblockingqueues-insert-and-remove-methods-thread-safe
    LinkedBlockingQueue<Request> ReqQueue;
    ConcurrentHashMap<String, Client> Clients;
    Accepter accepter;

    RequestProcessor[] requestProcessors;

    public ConnectionRelay() throws IOException {
        // more detail of ThreadPool please see
        // https://www.cnblogs.com/richaaaard/p/6599184.html
        // and
        // https://www.ibm.com/developerworks/cn/java/j-nio2-1/
        group = AsynchronousChannelGroup.withFixedThreadPool(2, Executors.defaultThreadFactory());
        accepter = null;
        ReqQueue = new LinkedBlockingQueue<>();
        Clients = new ConcurrentHashMap<>();
        requestProcessors = new RequestProcessor[1];
        for (int i = 0; i < requestProcessors.length; i++) {
            requestProcessors[i] = new RequestProcessor(ReqQueue, Clients);
        }

    }

    public void Sratr(String host, int port) throws IOException {
        Server = AsynchronousServerSocketChannel.open(group);
        Server.bind(new InetSocketAddress(host, port));

        accepter = new Accepter(Server, ReqQueue);

        for (RequestProcessor requestProcessor : requestProcessors) {
            requestProcessor.start();
        }
        Server.accept(new Request(ReqQueue), accepter);
    }

}