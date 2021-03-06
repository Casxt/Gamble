package Client;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Client {
    public String Name;
    public String Token;
    public int Chips;
    AsynchronousSocketChannel ch;
    Reader reader;
    private ConcurrentHashMap<String, Client> Clients;
    private Writer writer;

    public Client(String Name, AsynchronousSocketChannel ch, ConcurrentHashMap<String, Client> Clients) {
        Chips = 100;
        UUID uuid = UUID.randomUUID();
        Token = uuid.toString();
        this.ch = ch;
        this.Name = Name;
        this.Clients = Clients;
        reader = new Reader(this);
        writer = new Writer(this);
    }

    /**
     * Send A Msg to Client
     *
     * @param Msg should be complete, It will be send directly
     * @return whether the msg write into the waiting buff
     */
    public boolean Send(JSONObject Msg) {
        return writer.Write(Msg.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    /**
     * Close will close the socket and remove itself from UserList
     */
    public void Close() {
        try {
            ch.close();
            Clients.remove(Name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * if keepopen = true, the ch will not be closed after response send,
     * otherwise the ch will be close, default is false.
     *
     * @param keepOpen is the flag of keepOpen
     */
    public void KeepOpen(boolean keepOpen) {
        writer.keepOpen = keepOpen;
    }
}
