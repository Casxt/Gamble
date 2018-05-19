import Client.Client;
import Request.Request;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

class ConnectionRelay {
    // LinkedBlockingQueue are Thread safe, see:
    // https://stackoverflow.com/questions/2695426/are-linkedblockingqueues-insert-and-remove-methods-thread-safe
    LinkedBlockingQueue<Request> ReqQueue;
    ConcurrentHashMap<String, Client> Clients;
    AsynchronousServerSocketChannel server;
    private AsynchronousChannelGroup group;
    private Acceptor acceptor;

    ConnectionRelay() throws IOException {
        // more detail of ThreadPool please see
        // https://www.cnblogs.com/richaaaard/p/6599184.html
        // and
        // https://www.ibm.com/developerworks/cn/java/j-nio2-1/
        group = AsynchronousChannelGroup.withFixedThreadPool(2, Executors.defaultThreadFactory());
        acceptor = null;
        ReqQueue = new LinkedBlockingQueue<>();
        Clients = new ConcurrentHashMap<>();
    }

    void Sratr(String host, int port) throws IOException {
        server = AsynchronousServerSocketChannel.open(group);
        server.bind(new InetSocketAddress(host, port));

        acceptor = new Acceptor(server, ReqQueue);
        server.accept(new Request(ReqQueue), acceptor);
    }

    void ShotdownNow() throws IOException {
        server.close();
        group.shutdownNow();
    }

}