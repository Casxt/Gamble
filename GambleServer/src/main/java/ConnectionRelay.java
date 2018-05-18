import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionRelay {
    AsynchronousChannelGroup group;
    AsynchronousServerSocketChannel server;
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
        accepter = new Accepter();
        ReqQueue = new LinkedBlockingQueue<>();

        requestProcessors = new RequestProcessor[1];
        for(int i = 0; i<requestProcessors.length; i++){
            requestProcessors[i] = new RequestProcessor(ReqQueue, Clients);
        }

    }

    public void Sratr(String host, int port) throws IOException {
        server = AsynchronousServerSocketChannel.open(group);
        server.bind(new InetSocketAddress(host, port));

        for (RequestProcessor requestProcessor : requestProcessors) {
            requestProcessor.start();
        }
        server.accept(new Request(ReqQueue),accepter);
    }

}