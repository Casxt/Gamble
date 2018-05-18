import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class RequestProcessor implements Runnable  {
    private Thread thread;
    private LinkedBlockingQueue<Request> ReqQueue;
    ConcurrentHashMap<String, Client> Clients;
    public RequestProcessor(LinkedBlockingQueue<Request> ReqQueue, ConcurrentHashMap<String, Client> Clients){
        this.ReqQueue = ReqQueue;
        this.Clients = Clients;
    }

    public void run(){
        while (true) {
            try {
                JSONObject res;
                //take is a blocking method
                Request req = ReqQueue.take();

                switch (req.body.getString("Action")) {
                    case "Login":
                        res = UserLogin(req);
                        break;

                    default:
                        break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void start () {
        thread = new Thread (this);
        thread.start();
    }

    public JSONObject UserLogin(Request req){

        JSONObject reqData = req.body;
        JSONObject res = new JSONObject();
        String name = reqData.getString("Name");
        if (!Clients.containsKey(name)){
            Client c = new Client(name, req.ch);
            Clients.put(name, c);
            res.put("State", "Success")
                    .put("Msg", "User %s Login Successful")
                    .put("Token", c.Token);
        }
        res.put("State", "Failed")
                .put("Msg", "User Name Already Exist");

        return res;
    }
}
