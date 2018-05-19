package Client;

import com.sun.org.apache.xpath.internal.Arg;
import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

public class MsgTool {
    ConcurrentHashMap<String, Client> Clients;

    public MsgTool(ConcurrentHashMap<String, Client> Clients) {
        this.Clients = Clients;
    }

    public void Boardcast(String Action, String Msg, String ...Args) {
        JSONObject JsonMsg = new JSONObject();
        JsonMsg.put("Action", Action)
                .put("Msg", Msg);
        for (int i = 0; i < Args.length; i+=2) {
            JsonMsg.put(Args[i], Args[i+1]);
        }
        for (Client client : Clients.values()) {
            client.Send(JsonMsg);
        }
    }

    public void Boardcast(JSONObject JsonMsg) {
        for (Client client : Clients.values()) {
            client.Send(JsonMsg);
        }
    }
}
