package Client;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;

public class Client {
    AsynchronousSocketChannel ch;

    public Client(AsynchronousSocketChannel ch){
        this.ch = ch;
    }

    public void Quite(){
        System.out.println("您已下线。");
        try {
            ch.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
