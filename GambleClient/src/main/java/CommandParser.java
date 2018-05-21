import Client.Client;
import Request.Request;
import org.json.JSONObject;

import java.net.SocketAddress;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CommandParser {
    private static Logger log = Logger.getLogger(Request.class.getName());
    private Client client;
    private SocketAddress address;

    CommandParser(Client client, SocketAddress address) {
        this.address = address;
        this.client = client;
    }

    /**
     * Parse the user input, if return true, the program will try to restart,
     * false will closed
     *
     * @param cmd is user input
     */
    boolean Parse(String cmd) {

        if (cmd.isEmpty()) {
            return false;
        }

        if (client.IsWorking) {
            Matcher matcher = Pattern.compile("^(\\d+) ([D|X])$").matcher(cmd);
            if (matcher.find()) {

                if (!client.IsJoin) {
                    JoinGamble(Integer.parseInt(matcher.group(1)), matcher.group(2).equals("D"));
                } else {
                    System.out.println("你已经参加了本轮游戏");
                }
            } else {
                System.out.println(String.format("你说啥？要按套路出牌哦！您有%s个筹码，请下注：", client.Chips));
            }
        } else {
            return cmd.toLowerCase().equals("c");
    }
        return false;
    }


    /**
     * JoinGamble
     *
     * @param num     is chip to spend
     * @param betType is the type be chosen
     */
    private void JoinGamble(int num, boolean betType) {
        if (client.Chips >= num) {
            Request req = new Request(address);

            JSONObject jsonMsg = req.BaseObject("JoinGamble", client.Name, client.Token)
                    .put("SpendChips", num)
                    .put("BetType", betType);

            JSONObject res = req.Send(jsonMsg);

            if (res.getString("State").equals("Success")) {
                client.IsJoin = true;
                client.Chips -= num;
            } else {
                log.info(res.getString("Msg"));
            }

        } else {
            System.out.println(String.format("你行不行啊？你有那么多筹码吗？您有%s个筹码，请下注：", client.Chips));
        }
    }
}
