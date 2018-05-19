import Client.Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class GambleClient {
    private static AsynchronousSocketChannel serverCh;
    private static Client client;
    private static SocketAddress Addr = new InetSocketAddress("127.0.0.1",12345);

    public static void main(String[] args) {

        System.out.println("连接中...");

        Connect();
        client = new Client(serverCh, Addr);
        System.out.println("连接成功，请输入用户名：");

        while (!client.Login()){
            client.Close();
            Connect();
            client = new Client(serverCh, Addr);
        }
        client.Start();


        System.out.println("您有100个筹码，请下注：");

        Scanner sc = new Scanner(System.in);

        CommandParser commandParser = new CommandParser(client, Addr);
        while (true){
            commandParser.Parse(sc.nextLine());
        }

    }

    private static void Connect(){
        try {
            serverCh = AsynchronousSocketChannel.open();
            Future future = serverCh.connect(Addr);
            future.get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
