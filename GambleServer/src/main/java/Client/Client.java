package Client;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Client {
    String Name;
    AsynchronousSocketChannel ch;
    public String Token;
    ConcurrentHashMap<String, Client> Clients;

    int Chips;

    public Client(String Name, AsynchronousSocketChannel ch, ConcurrentHashMap<String, Client> Clients){
        Chips = 100;
        UUID uuid = UUID.randomUUID();
        Token = uuid.toString();
        this.ch = ch;
        this.Name = Name;
        this.Clients = Clients;
    }

    /**
     * Close will close the socket and remove itself from UserList
     */
    public void Close(){
        try {
            ch.close();
            Clients.remove(Name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
