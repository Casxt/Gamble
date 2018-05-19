package Game;

import Client.Client;
import org.json.JSONObject;

enum BetType {
    Big, Small
}

public class Gambler {
    Client client;
    int chips = 0;
    BetType betType;

    Gambler(Client client, int chips, BetType betType) {
        this.client = client;
        this.chips = chips;
        this.betType = betType;
    }

    public int Win() {
        //因为之前没有扣除，所以这里只加一倍
        client.Chips += chips;
        JSONObject res = new JSONObject()
                .put("Action", "GambleResultNotify")
                .put("Msg", String.format("You have win %d gamble", chips * 2))
                .put("Res", "Win")
                .put("ChangeNum", chips * 2)
                .put("Remain", client.Chips);
        client.Send(res);
        return chips;
    }

    public int Lose() {
        client.Chips -= chips;
        JSONObject res = new JSONObject()
                .put("Action", "GambleResultNotify")
                .put("Msg", String.format("You have lost %d gamble", chips))
                .put("Res", "Lost")
                .put("ChangeNum", chips)
                .put("Remain", client.Chips);
        client.Send(res);
        return chips;
    }
}
