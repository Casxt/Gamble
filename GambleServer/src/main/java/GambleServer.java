import Game.Game;

import java.io.IOException;
import java.util.logging.Logger;

public class GambleServer {
    private static String name = GambleServer.class.getName();
    private static Logger log = Logger.getLogger(name);
    private static RequestProcessor[] requestProcessors;

    public static void main(String[] args) {

        try {
            ConnectionRelay server = new ConnectionRelay();

            Game game = new Game(server.Clients);

            requestProcessors = new RequestProcessor[1];
            for (int i = 0; i < requestProcessors.length; i++) {
                requestProcessors[i] = new RequestProcessor(server.ReqQueue, server.Clients, game);
            }
            for (RequestProcessor requestProcessor : requestProcessors) {
                requestProcessor.Start();
            }

            server.Start("0.0.0.0", 12345);

            Thread gameThread = game.Start();

            log.info("Server Starting......");
            gameThread.join();
            server.ShutdownNow();
            for (RequestProcessor requestProcessor : requestProcessors) {
                requestProcessor.thread.interrupt();
            }

        } catch (IOException e) {
            e.printStackTrace();
            log.severe("Server Start Failed");
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.severe("Server Exit");
        }

    }
}
