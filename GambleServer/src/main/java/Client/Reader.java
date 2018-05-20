package Client;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.logging.Logger;

/**
 * Reader is mainly use to detect the err of client
 */
public class Reader implements CompletionHandler<Integer, AsynchronousSocketChannel> {
    private static Logger log = Logger.getLogger(Reader.class.getName());
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
            log.info(String.format("client %s Timeout interrupted and closed", client.Name));
        } else if (e instanceof java.io.IOException){
            //java.io.IOException: 远程主机强迫关闭了一个现有的连接。
            log.info(String.format("client %s remote closed", client.Name));
        } else {
            log.info("client closed");
            e.printStackTrace();
        }
        client.Close();
    }
}
