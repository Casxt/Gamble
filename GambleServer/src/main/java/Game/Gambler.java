package Game;

import Client.Client;
enum BetType {
    Big, Small
}
public class Gambler {
    Client client;
    int chips;
    BetType  betType;

    Gambler(Client client, int chips, BetType betType){
        this.client = client;
        this.chips = chips;
        this.betType = betType;
    }
    
}
