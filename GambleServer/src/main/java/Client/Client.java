package Client;

import java.nio.channels.AsynchronousChannel;
import java.util.UUID;

public class Client {
    String Name;
    AsynchronousChannel ch;
    public String Token;
    int Chips;

    public Client(String Name, AsynchronousChannel ch){
        Chips = 100;
        UUID uuid = UUID.randomUUID();
        Token = uuid.toString();
        this.ch = ch;
        this.Name = Name;
    }
}
