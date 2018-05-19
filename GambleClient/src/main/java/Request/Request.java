package Request;

import PackTool.PackTool;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class Request {
    private static Logger log = Logger.getLogger(Request.class.getName());
    private PackTool packer = new PackTool(new byte[]{'G', 'r', 'a', 'm', 'b', 'l', 'e'});
    private SocketAddress address;
    private AsynchronousSocketChannel ch;

    public Request(SocketAddress address) {
        this.address = address;
    }

    public JSONObject BaseObject(String Action, String Name, String Token) {
        JSONObject JsonMsg = new JSONObject();
        JsonMsg.put("Action", Action)
                .put("Name", Name)
                .put("Token", Token);
        return JsonMsg;
    }

    public JSONObject Send(JSONObject JsonMsg) {
        try {
            ch = AsynchronousSocketChannel.open();
            ch.connect(address).get();
        } catch (InterruptedException | ExecutionException | IOException e) {
            log.info("Request Conn Failed");
        }

        ByteBuffer buff = packer.DataConstructor(JsonMsg.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        try {
            ch.write(buff).get();
        } catch (InterruptedException | ExecutionException e) {
            log.info("Request Send Failed");
        }

        buff = ByteBuffer.allocate(2048);

        try {
            ch.read(buff).get();
            ch.close();
            buff.flip();
            byte[] data = packer.DataDeconstructor(buff);

            if (data == null) {
                log.info("Request Send Failed");
                return null;
            }

            String s = new String(data, java.nio.charset.StandardCharsets.UTF_8);
            return new JSONObject(s);

        } catch (InterruptedException | ExecutionException | IOException e) {
            log.info("Request Read Failed");
        }
        return null;
    }
}
