import Client.Client;
import Request.Request;
import org.json.JSONObject;

import java.net.SocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser{
    Client client;
    SocketAddress Addr;
    public CommandParser(Client client, SocketAddress Addr){
        this.Addr = Addr;
        this.client = client;
    }

    public void Parase(String cmd){

        String pattern = "^(\\d+) ([D|X])$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(cmd);
        int num;
        boolean betType;
        if (m.find()){
            num = Integer.parseInt(m.group(1));
            if (m.group(2).equals("D")){
                betType = true;
            } else {
                betType = false;
            }
        } else {
            System.out.println(String.format("你说啥？要按套路出牌哦！您有%s个筹码，请下注：", client.Chips));
            return;
        }

        if (client.Chips >= num){
            Request req = new Request(Addr);
            JSONObject jsonMsg = req.BaseObject("JoinGame", client.Name, client.Token)
                    .put("SpendChips", num)
                    .put("BetType", betType);
            req.Send(jsonMsg);
            client.Chips -= num;
        } else {
            System.out.println(String.format("你行不行啊？你有那么多筹码吗？您有%s个筹码，请下注：", client.Chips));
            return;
        }

    }

}
