package MsgHandle;

import Client.Client;
import org.json.JSONObject;

public class MsgHandle {

    private Client client;

    public MsgHandle(Client client) {
        this.client = client;
    }

    public void Parse(byte[] data) {
        String s = new String(data, java.nio.charset.StandardCharsets.UTF_8);
        JSONObject Msg = new JSONObject(s);
        switch (Msg.getString("Action")) {
            case "LoginNotify":
                LoginNotify(Msg);
                break;
            case "GamblePrepareNotify":
                GamblePrepareNotify(Msg);
                break;
            case "GambleStartNotify":
                GambleStartNotify(Msg);
                break;
            case "GambleNumNotify":
                GambleNumNotify(Msg);
                break;
            case "GambleResultNotify":
                GambleResultNotify(Msg);
                break;
            case "GambleServerChipEmptyNotify":
                GambleServerChipEmptyNotify(Msg);
                break;
            default:
                break;

        }
    }

    public void LoginNotify(JSONObject Msg) {
        System.out.println(String.format("玩家 %s 加入游戏", Msg.getString("Name")));
    }

    public void GamblePrepareNotify(JSONObject Msg) {
        System.out.println("开始啦！大家快下注啦！赌大小啊！翻倍赢啊！");
        System.out.println(String.format("您有%s个筹码，请下注：", client.Chips));
    }

    public void GambleStartNotify(JSONObject Msg) {
        System.out.println("停止下注啦！都不要动啦！马上要开啦！开！开！开！");
    }

    public void GambleNumNotify(JSONObject Msg) {
        System.out.println(String.format("%s点！", Msg.getString("Msg")));
    }

    public void GambleResultNotify(JSONObject Msg) {
        System.out.println(String.format("%s ", Msg.getString("Msg")));
    }

    public void GambleServerChipEmptyNotify(JSONObject Msg) {
        System.out.println(String.format("%s", Msg.getString("Msg")));
    }
}
