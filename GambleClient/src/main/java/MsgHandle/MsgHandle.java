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
            case "GamblerJoinNotify":
                GamblerJoinNotify(Msg);
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
            case "GambleUserChipEmptyNotify":
                GambleUserChipEmptyNotify(Msg);
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

    public void GamblerJoinNotify(JSONObject Msg) {
        System.out.println(String.format("%s下注%s个，押%s！",
                Msg.getString("Name").equals(client.Name)?"你":Msg.getString("Name"),
                Msg.getInt("SpendChips"),
                Msg.getBoolean("BetType")?"大":"小"));
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

        if (Msg.getString("Res").equals("Win")) {
            client.Chips = Msg.getInt("Remain");
            System.out.println(String.format("你赢了，返还双倍共%s个筹码.", Msg.getInt("ChangeNum")));
        } else {
            client.Chips = Msg.getInt("Remain");
            System.out.println(String.format("你输了，%s个筹码都归了庄家。", Msg.getInt("ChangeNum")));
        }

    }

    public void GambleUserChipEmptyNotify(JSONObject Msg) {
        if (Msg.getString("Name").equals(client.Name)) {
            //你输个精光，别玩儿了！
            System.out.println("你输个精光，别玩儿了！");
        } else {
            System.out.println(String.format("%s输个精光，被一脚踢出！", Msg.getString("Name")));
        }

    }

    public void GambleServerChipEmptyNotify(JSONObject Msg) {
        System.out.println(String.format("庄家运气怎么这么差，竟然输光了，掀桌子不玩儿了！大家散场啦！", Msg.getString("Msg")));
    }
}
