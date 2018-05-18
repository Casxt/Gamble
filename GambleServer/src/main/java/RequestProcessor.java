import org.json.JSONObject;

import java.util.concurrent.LinkedBlockingQueue;

public class RequestProcessor implements Runnable  {
    private Thread thread;
    private LinkedBlockingQueue<Request> ReqQueue;
    public RequestProcessor(LinkedBlockingQueue<Request> ReqQueue){
        this.ReqQueue = ReqQueue;
    }

    public void run(){
        //take is a blocking method
        try {
            Request req = ReqQueue.take();
            switch (req.body.getString("Action")){
                case "Login":
                    UserLogin(req.body);
                    break;

                    default:
                        break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start () {
        thread = new Thread (this);
        thread.start ();
    }

    public JSONObject UserLogin(JSONObject ActionData){
        JSONObject res = new JSONObject();

        return res;
    }
}
