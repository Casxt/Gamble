package Game;

import Client.Client;
import org.json.JSONObject;

class Gambler {
    Client client;
    BetType betType;
    private int chips;

    Gambler(Client client, int chips, BetType betType) {
        this.client = client;
        this.chips = chips;
        this.betType = betType;
    }

    int Win() {
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

    int Lose() {
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
