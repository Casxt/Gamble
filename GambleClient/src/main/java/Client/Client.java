package Client;

import PackTool.PackTool;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Client {
    AsynchronousSocketChannel ch;
    public Reader reader;
    public String Name;
    public String Token;
    public int Chips;
    public Client(AsynchronousSocketChannel ch) {
        this.ch = ch;
        reader = new Reader(this);
        Chips = 100;
    }

    public void Start() {
        ch.read(reader.Buff, 20, TimeUnit.SECONDS, ch, reader);
    }

    public void Quite() {
        System.out.println("您已下线。");
        Close();
    }

    public void Close(){
        try {
            ch.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean Login(){

        Scanner sc = new Scanner(System.in);
        Name = sc.nextLine();
        JSONObject req = new JSONObject();

        req.put("Action", "Login")
                .put("Name", Name);

        try {
            PackTool packer = new PackTool(new byte[] {'G','r','a','m','b','l','e'});
            ByteBuffer buff = packer.DataConstructor(req.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
            Future future = ch.write(buff);
            future.get();

            buff = ByteBuffer.allocate(2048);
            future = ch.read(buff);
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
                Token = res.getString("Token");
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
