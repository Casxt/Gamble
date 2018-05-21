import Client.Client;
import Client.MsgTool;
import Game.Game;
import Request.Request;
import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class RequestProcessor implements Runnable {
    private static Logger log = Logger.getLogger(RequestProcessor.class.getName());
    Thread thread;
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
                //Stop Thread
                log.info("RequestProcessor exit");
                return;
                //e.printStackTrace();
            }
        }
    }

    void Start() {
        thread = new Thread(this);
        thread.start();
    }

    /**
     * {"Action":"Login",
     * "Name":Something}
     *
     * @param req should contain data above
     * @return response
     */
    private JSONObject UserLogin(Request req) {

        JSONObject reqData = req.body;
        JSONObject res = new JSONObject();
        String name = reqData.getString("Name");
        if (!Clients.containsKey(name)) {
            Client c = new Client(name, req.ch, Clients);
            Clients.put(name, c);
            msgTool.BroadcastExcept(name, "LoginNotify", String.format("User %s Login", name), "Name", name);
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
     * "BetType":bool, true = big, false = small
     * "Token":""}
     *
     * @param req should contain data above
     * @return response
     */
    private JSONObject JoinGamble(Request req) {
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

                        JSONObject jsonMsg = new JSONObject();
                        jsonMsg.put("Action", "GamblerJoinNotify")
                                .put("Msg", String.format("%s join Gamble", c.Name))
                                .put("Name", c.Name)
                                .put("SpendChips", SpendChips)
                                .put("BetType", reqData.getBoolean("BetType"));
                        msgTool.Broadcast(jsonMsg);

                        //此处不扣除，等结果确定后再处理
                        //c.Chips -= SpendChips;
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
