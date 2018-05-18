import java.io.IOException;
import java.util.logging.Logger;

public class GambleServer {
    private static String name = GambleServer.class.getName();
    private static Logger log = Logger.getLogger(name);

    public static void main(String[] args) {

        try {
            ConnectionRelay server = new ConnectionRelay();
            server.Sratr("0.0.0.0",12345);
            log.info("Server Starting......");
        } catch (IOException e) {
            e.printStackTrace();
            log.severe("Server Start Failed");
        }

    }
}
