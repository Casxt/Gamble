package Request;

import Client.Client;
import PackTool.PackTool;
import org.json.JSONObject;

import java.net.SocketAddress;

public class Request {
    PackTool packer;
    static SocketAddress Addr;

    public void Send(String Action, String Msg, String ...Args) {
        JSONObject JsonMsg = new JSONObject();
        JsonMsg.put("Action", Action)
                .put("Msg", Msg);
        for (int i = 0; i < Args.length; i+=2) {
            JsonMsg.put(Args[i], Args[i+1]);
        }

        JsonMsg.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);

    }
}
