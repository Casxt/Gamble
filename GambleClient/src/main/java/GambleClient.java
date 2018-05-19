import Client.Client;
import PackTool.PackTool;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class GambleClient {
    static AsynchronousSocketChannel serverCh;
    static Client client;
    private static CommandParser commandParser = new CommandParser();
    public static void main(String[] args) {

        System.out.println("Connecting...");

        Connect();
        client = new Client(serverCh);
        System.out.println("连接成功，请输入用户名：");

        while (!client.Login()){
            client.Close();
            Connect();
            client = new Client(serverCh);
        }
        client.Start();

        System.out.println("您有100个筹码，请下注：");
        
        Scanner sc = new Scanner(System.in);

        while (true){
            commandParser.Parase(sc.nextLine());
        }

    }

    public static void Connect(){
        try {
            serverCh = AsynchronousSocketChannel.open();
            Future future = serverCh.connect(new InetSocketAddress("127.0.0.1",12345));
            future.get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static boolean Login(){

        Scanner sc = new Scanner(System.in);
        String Name = sc.nextLine();
        JSONObject req = new JSONObject();

        req.put("Action", "Login")
                .put("Name", Name);

        try {
            PackTool packer = new PackTool(new byte[] {'G','r','a','m','b','l','e'});
            ByteBuffer buff = packer.DataConstructor(req.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
            Future future = serverCh.write(buff);
            future.get();

            buff = ByteBuffer.allocate(2048);
            future = serverCh.read(buff);
            future.get();
            buff.flip();
            String t = new String(buff.array());
            System.out.println(t);
            byte[] data = packer.DataDeconstructor(buff);
            if (data == null){
                System.out.println("数据发送失败，请再次输入用户名：");
                return false;
            }

            String s = new String(data, java.nio.charset.StandardCharsets.UTF_8);
            JSONObject res = new JSONObject(s);

            if(res.getString("State").equals("Success")){
                return true;
            } else {
                System.out.println("用户名已经存在，请更换一个新名字：");
                return false;
            }

        } catch (InterruptedException | ExecutionException e) {
            System.out.println("数据读取异常，请再次输入用户名：");
            e.printStackTrace();
            return false;
        }

    }
}
