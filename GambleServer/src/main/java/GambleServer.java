import Game.Game;

import java.io.IOException;
import java.util.logging.Logger;

public class GambleServer {
    private static String name = GambleServer.class.getName();
    private static Logger log = Logger.getLogger(name);
    public static void main(String[] args) {

        try {
            ConnectionRelay server = new ConnectionRelay();
            server.Sratr("0.0.0.0",12345);
            Game game = new Game(server.Clients);
            Thread gameThread = game.Start();
            log.info("Server Starting......");
            gameThread.join();
        } catch (IOException e) {
            e.printStackTrace();
            log.severe("Server Start Failed");
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.severe("Server Exit");
        }

    }
}
