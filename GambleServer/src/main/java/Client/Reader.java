package Client;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.logging.Logger;

/**
 * Reader is mainly use to detect the err or client
 */
public class Reader implements CompletionHandler<Integer, AsynchronousSocketChannel> {
    private static String name = Reader.class.getName();
    private static Logger log = Logger.getLogger(name);
    private Client client;

    Reader(Client client) {
        this.client = client;
    }

    @Override
    public void completed(Integer result, AsynchronousSocketChannel ch) {
        if (result != -1) {
            ch.read(null, ch, this);
        } else {
            log.info(String.format("client %s read -1 and closed", client.Name));
            client.Close();
        }
    }


    @Override
    public void failed(Throwable e, AsynchronousSocketChannel ch) {
        if (e instanceof java.nio.channels.InterruptedByTimeoutException) {
            client.Close();
            log.info(String.format("client %s Timeout and closed", client.Name));
        } else {
            log.info("client closed");
            client.Close();
            e.printStackTrace();
        }
    }
}
