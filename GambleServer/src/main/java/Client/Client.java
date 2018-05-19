package Client;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Client {
    String Name;
    AsynchronousSocketChannel ch;
    public String Token;
    ConcurrentHashMap<String, Client> Clients;
    ClientWriter writer;
    ClientReader reader;
    int Chips;

    public Client(String Name, AsynchronousSocketChannel ch, ConcurrentHashMap<String, Client> Clients){
        Chips = 100;
        UUID uuid = UUID.randomUUID();
        Token = uuid.toString();
        this.ch = ch;
        this.Name = Name;
        this.Clients = Clients;
        reader = new ClientReader(this);
        writer = new ClientWriter(this);
    }

    /**
     * Send A Msg to Client
     * @param Msg should be complete, It will be send directly
     * @return whether the msg write into the waiting buff
     */
    public boolean Send(JSONObject Msg) {
        return writer.Write(Msg.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
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
