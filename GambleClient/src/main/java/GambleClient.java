import jdk.nashorn.api.scripting.JSObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class GambleClient {
    static AsynchronousSocketChannel serverCh;
    public static void main(String[] args) {
        Connect();
        Login();


    }

    public static void Connect(){
        System.out.println("Connecting...");

        try {
            serverCh = AsynchronousSocketChannel.open();
            Future future = serverCh.connect(new InetSocketAddress("127.0.0.1",12345));
            future.get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    public static void Login(){
        System.out.println("连接成功，请输入用户名：");

    }
}
