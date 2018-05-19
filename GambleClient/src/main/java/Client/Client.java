package Client;

import PackTool.PackTool;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Client {
    private AsynchronousSocketChannel ch;
    private Reader reader = new Reader(this);
    public String Name;
    public String Token;
    public int Chips = 100;
    public boolean IsJoin = false;
    public boolean IsWorking = false;

    public Client(AsynchronousSocketChannel ch) {
        this.ch = ch;
    }

    public void Start() {
        ch.read(reader.Buff, 35, TimeUnit.SECONDS, ch, reader);
    }

    void Quite() {
        Close();
        System.out.println("您已掉线，c键重连，其他键退出：");
    }

    public void Close() {
        IsWorking = false;
        if (ch != null) {
            try {
                ch.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean Login() {
        System.out.println("连接成功，请输入用户名：");
        Scanner scanner = new Scanner(System.in);
        Name = scanner.nextLine();

        if (!Pattern.matches("^\\S{1,32}$", Name)) {
            System.out.println("无效输入，请重新输入用户名：");
            return false;
        }

        JSONObject req = new JSONObject();

        req.put("Action", "Login")
                .put("Name", Name);

        try {
            PackTool packer = new PackTool(new byte[]{'G', 'r', 'a', 'm', 'b', 'l', 'e'});
            ByteBuffer buff = packer.DataConstructor(req.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
            ch.write(buff).get();

            buff = ByteBuffer.allocate(2048);
            ch.read(buff).get();
            buff.flip();

            byte[] data = packer.DataDeconstructor(buff);
            if (data == null) {
                System.out.println("数据发送失败，请再次输入用户名：");
                return false;
            }

            String s = new String(data, java.nio.charset.StandardCharsets.UTF_8);
            JSONObject res = new JSONObject(s);

            if (res.getString("State").equals("Success")) {
                Token = res.getString("Token");
                IsWorking = true;
                return true;
            } else {
                System.out.println("用户名已经存在，请更换一个新名字：");
                return false;
            }

        } catch (InterruptedException | ExecutionException e) {
            System.out.println("连接异常，请再次输入用户名：");
            e.printStackTrace();
            return false;
        }

    }


}
