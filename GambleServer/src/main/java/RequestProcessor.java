import Client.Client;
import Client.MsgTool;
import Request.Request;
import org.json.JSONObject;
import Game.Game;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class RequestProcessor implements Runnable {
    private Thread thread;
    private LinkedBlockingQueue<Request> ReqQueue;
    private ConcurrentHashMap<String, Client> Clients;
    private MsgTool msgTool;
    private Game game;

    RequestProcessor(LinkedBlockingQueue<Request> ReqQueue, ConcurrentHashMap<String, Client> Clients, Game game) {
        this.ReqQueue = ReqQueue;
        this.Clients = Clients;
        this.game = game;
        msgTool = new MsgTool(Clients);
    }

    public void run() {
        while (true) {
            try {
                JSONObject res;
                //take is a blocking method
                Request req = ReqQueue.take();

                switch (req.body.getString("Action")) {
                    case "Login":
                        res = UserLogin(req);
                        break;
                    case "JoinGamble":
                        res = JoinGamble(req);
                        break;
                    default:
                        res = new JSONObject();
                        res.put("State", "Failed")
                                .put("Msg", "Invalid Action");
                        break;
                }

                req.Response(res);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Thread Start() {
        thread = new Thread(this);
        thread.start();
        return thread;
    }

    /**
     * {"Action":"Login",
     * "Name":Something}
     *
     * @param req
     * @return
     */
    public JSONObject UserLogin(Request req) {

        JSONObject reqData = req.body;
        JSONObject res = new JSONObject();
        String name = reqData.getString("Name");
        if (!Clients.containsKey(name)) {
            Client c = new Client(name, req.ch, Clients);
            Clients.put(name, c);
            msgTool.BoardcastExcept(name, "LoginNotify", String.format("User %s Login", name), "Name", name);
            req.KeepOpen(true);
            res.put("State", "Success")
                    .put("Msg", String.format("User %s Login Successful", name))
                    .put("Token", c.Token);
            return res;
        }
        res.put("State", "Failed")
                .put("Msg", "User Name Already Exist");

        return res;
    }

    /**
     * {"Action":"JoinGamble",
     * "Name":"",
     * "SpendChips":Num,
     * "BetType":bool, ture = big, false = small
     * "Token":""}
     *
     * @param req
     * @return
     */
    public JSONObject JoinGamble(Request req) {
        JSONObject reqData = req.body;
        JSONObject res = new JSONObject();
        String name = reqData.getString("Name");
        String token = reqData.getString("Token");
        // Check User
        if (Clients.containsKey(name)) {
            Client c = Clients.get(name);

            // Check Token
            if (c.Token.equals(token)) {

                int SpendChips = reqData.getInt("SpendChips");

                // Check Chips
                if (c.Chips >= SpendChips) {


                    boolean success = game.Join(c, SpendChips, reqData.getBoolean("BetType"));

                    if (success) {
                        msgTool.Boardcast("GamblerJoinNotify", "%s join Gamble", "Name", c.Name);
                        c.Chips -= SpendChips;
                        res.put("State", "Success")
                                .put("Msg", "You Join the Game!")
                                .put("Chips", c.Chips);
                    } else {
                        res.put("State", "Failed")
                                .put("Msg", "Already Joined")
                                .put("Chips", c.Chips);
                    }

                } else {
                    res.put("State", "Failed")
                            .put("Msg", "Chips not Enough")
                            .put("Chips", c.Chips);
                }

            } else {
                res.put("State", "Failed")
                        .put("Msg", "Token Error");
            }

        } else {
            res.put("State", "Failed")
                    .put("Msg", "User Name Not Exist");
        }

        return res;
    }
}
