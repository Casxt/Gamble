package Client;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * This class is mainly use to detect the err or client
 */
public class ClientReader implements CompletionHandler<Integer, AsynchronousSocketChannel> {
    private Client client;

    public ClientReader(Client client) {
        this.client = client;
    }

    @Override
    public void completed(Integer result, AsynchronousSocketChannel ch) {
        if (result != -1){
            ch.read(null,ch,this);
        } else {
            client.Close();
        }
    }


    @Override
    public void failed(Throwable e, AsynchronousSocketChannel ch) {
        // if Client.Client Closed, may cause this err
        client.Close();
        e.printStackTrace();
    }
}
