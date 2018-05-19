package Client;

import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

public class MsgTool {
    ConcurrentHashMap<String, Client> Clients;

    public MsgTool(ConcurrentHashMap<String, Client> Clients) {
        this.Clients = Clients;
    }

    public void BoardcastExcept(String name, String Action, String Msg, String... Args) {
        JSONObject JsonMsg = new JSONObject();
        JsonMsg.put("Action", Action)
                .put("Msg", Msg);
        for (int i = 0; i < Args.length; i += 2) {
            JsonMsg.put(Args[i], Args[i + 1]);
        }
        for (Client client : Clients.values()) {
            if (!client.Name.equals(name)) {
                client.Send(JsonMsg);
            }
        }
    }

    public void BoardcastExcept(String name, JSONObject JsonMsg) {
        for (Client client : Clients.values()) {
            if (!client.Name.equals(name)) {
                client.Send(JsonMsg);
            }
        }
    }

    public void Boardcast(String Action, String Msg, String... Args) {
        JSONObject JsonMsg = new JSONObject();
        JsonMsg.put("Action", Action)
                .put("Msg", Msg);
        for (int i = 0; i < Args.length; i += 2) {
            JsonMsg.put(Args[i], Args[i + 1]);
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

    public void BoardcastLastMsg(String Action, String Msg, String... Args) {
        JSONObject JsonMsg = new JSONObject();
        JsonMsg.put("Action", Action)
                .put("Msg", Msg);
        for (int i = 0; i < Args.length; i += 2) {
            JsonMsg.put(Args[i], Args[i + 1]);
        }

        for (Client client : Clients.values()) {
            client.KeepOpen(false);
            client.Send(JsonMsg);
        }
    }
}
