import Client.Client;
import Request.Request;
import org.json.JSONObject;

import java.net.SocketAddress;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CommandParser {
    private Client client;
    private SocketAddress Addr;
    private static Logger log = Logger.getLogger(Request.class.getName());

    private boolean isJoin = false;

    CommandParser(Client client, SocketAddress Addr) {
        this.Addr = Addr;
        this.client = client;
    }

    /**
     * Parse the user input
     *
     * @param cmd is user input
     */
    void Parse(String cmd) {

        Matcher matcher = Pattern.compile("^(\\d+) ([D|X])$").matcher(cmd);

        if (matcher.find()) {

            if (!isJoin){
                JoinGamble(Integer.parseInt(matcher.group(1)), matcher.group(2).equals("D"));
            } else {
                System.out.println("你已经参加了本轮游戏");
            }

        } else {
            System.out.println(String.format("你说啥？要按套路出牌哦！您有%s个筹码，请下注：", client.Chips));
        }


    }


    /**
     * JoinGamble
     *
     * @param num     is chip to spend
     * @param betType is the type be chosen
     */
    private void JoinGamble(int num, boolean betType) {
        if (client.Chips >= num) {
            Request req = new Request(Addr);

            JSONObject jsonMsg = req.BaseObject("JoinGamble", client.Name, client.Token)
                    .put("SpendChips", num)
                    .put("BetType", betType);

            JSONObject res = req.Send(jsonMsg);

            if (res.getString("State").equals("Success")) {
                isJoin = true;
                client.Chips -= num;
            } else {
                log.info(res.getString("Msg"));
            }

        } else {
            System.out.println(String.format("你行不行啊？你有那么多筹码吗？您有%s个筹码，请下注：", client.Chips));
        }
    }
}
