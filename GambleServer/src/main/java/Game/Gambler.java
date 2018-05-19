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
        JSONObject res = new JSONObject()
                .put("Action", "GambleResultNotify")
                .put("Msg", String.format("You have win %d gamble", chips * 2))
                .put("Res", "Win")
                .put("ChangeNum", chips * 2)
                .put("Remain", client.Chips += chips * 2);
        client.Send(res);
        return chips;
    }

    public int Lose() {
        JSONObject res = new JSONObject()
                .put("Action", "GambleResultNotify")
                .put("Msg", String.format("You have lost %d gamble", chips))
                .put("Res", "Lost")
                .put("ChangeNum", chips)
                .put("Remain", client.Chips -= chips);
        client.Send(res);
        return chips;
    }
}
