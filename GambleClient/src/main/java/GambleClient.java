import Client.Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class GambleClient {


    public static void main(String[] args) {

        boolean restartFlag = false;
        SocketAddress address = new InetSocketAddress("127.0.0.1", 12345);
        Client client;
        AsynchronousSocketChannel serverCh;

        do {
            System.out.print("连接中");
            serverCh = Connect(address);
            client = new Client(serverCh);

            while (serverCh == null || !client.Login()) {
                System.out.print(".");
                client.Close();
                serverCh = Connect(address);
                client = new Client(serverCh);
            }
            client.Start();

            System.out.println("您有100个筹码，请下注：");
            Scanner sc = new Scanner(System.in);
            CommandParser commandParser = new CommandParser(client, address);

            while (client.IsWorking) {
                restartFlag = commandParser.Parse(sc.nextLine());
            }

        } while (restartFlag);

    }

    private static AsynchronousSocketChannel Connect(SocketAddress Addr) {
        AsynchronousSocketChannel serverCh;
        try {
            serverCh = AsynchronousSocketChannel.open();
            serverCh.connect(Addr).get();
            return serverCh;
        } catch (IOException | InterruptedException | ExecutionException e) {
            //System.out.println(e.toString().endsWith("远程计算机拒绝网络连接。"));
            //e.printStackTrace();
            return null;
        }
    }

}
