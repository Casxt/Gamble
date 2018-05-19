package Client;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.logging.Logger;

/**
 * This class is mainly use to detect the err or client
 */
public class Reader implements CompletionHandler<Integer, AsynchronousSocketChannel> {
    private Client client;

    private static String name = Reader.class.getName();
    private static Logger log = Logger.getLogger(name);

    public Reader(Client client) {
        this.client = client;
    }

    @Override
    public void completed(Integer result, AsynchronousSocketChannel ch) {
        if (result != -1){
            ch.read(null,ch,this);
        } else {
            log.info("client closed");
            client.Close();
        }
    }


    @Override
    public void failed(Throwable e, AsynchronousSocketChannel ch) {
        // if Client.Client Closed, may cause this err
        log.info("client closed");
        client.Close();
        //e.printStackTrace();
    }
}
