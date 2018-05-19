package MsgHandle;

import org.json.JSONObject;

public class MsgHandle {

    public static void Parse(byte[] data){
        String s = new String(data, java.nio.charset.StandardCharsets.UTF_8);
        JSONObject Msg = new JSONObject(s);
        switch (Msg.getString("Action")){
            case "LoginNotify":
                LoginNotify(Msg);
                break;

            default:
                break;

        }
    }

    public static void LoginNotify(JSONObject Msg){
        System.out.println(String.format("玩家 %s 加入游戏", Msg.getString("Name")));
    }
}
