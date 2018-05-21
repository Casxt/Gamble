package Client;

import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

public class MsgTool {
    private ConcurrentHashMap<String, Client> Clients;

    public MsgTool(ConcurrentHashMap<String, Client> Clients) {
        this.Clients = Clients;
    }

    /**
     * Send Msg Except the specific user
     *
     * @param name   of specific user
     * @param Action Action field
     * @param Msg    Msg field
     * @param Args   More Args, A pair of Key-Value
     */
    public void BroadcastExcept(String name, String Action, String Msg, String... Args) {
        assert Args.length % 2 == 0;
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

    public void BroadcastExcept(String name, JSONObject JsonMsg) {
        for (Client client : Clients.values()) {
            if (!client.Name.equals(name)) {
                client.Send(JsonMsg);
            }
        }
    }

    /**
     * Send Msg to all User
     *
     * @param Action Action field
     * @param Msg    Msg field
     * @param Args   More Args, A pair of Key-Value
     */
    public void Broadcast(String Action, String Msg, String... Args) {
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

    /**
     * Send Msg to all User
     *
     * @param JsonMsg The json will be send
     */
    public void Broadcast(JSONObject JsonMsg) {
        for (Client client : Clients.values()) {
            client.Send(JsonMsg);
        }
    }

    /**
     * Send Msg to all User and close All User
     *
     * @param Action Action field
     * @param Msg    Msg Action field
     * @param Args   More Args, A pair of Key-Value
     */
    public void BroadcastLastMsg(String Action, String Msg, String... Args) {
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
