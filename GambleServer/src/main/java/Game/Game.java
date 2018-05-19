package Game;

import Client.Client;
import Client.MsgTool;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Game implements Runnable {
    ConcurrentHashMap<String, Client> Clients;
    ConcurrentHashMap<String, Integer> UserChips;
    Random ran;
    MsgTool msgTool;
    private static String name = Game.class.getName();
    private static Logger log = Logger.getLogger(name);

    Game(ConcurrentHashMap<String, Client> Clients) {
        this.Clients = Clients;
        UserChips = new ConcurrentHashMap<>();
        // default use System.nanoTime()
        ran = new Random();
        msgTool = new MsgTool(Clients);
    }

    @Override
    public void run() {
        while (true) {
            // Delay
            try {
                //See more detail at http://www.importnew.com/7219.html
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                return;
            }

            msgTool.Boardcast("GambleStartNotify", "开始啦！大家快下注啦！赌大小啊！翻倍赢啊");


            // 锁定本局参与者
            ConcurrentHashMap<String, Integer> nowChips;
            synchronized (UserChips) {
                nowChips = UserChips;
                //TODO:先创建再直接赋值？
                UserChips = new ConcurrentHashMap<>();
            }

            // About uniformly distributed see more about https://stackoverflow.com/questions/20389890/generating-a-random-number-between-1-and-10-java
            // and https://docs.oracle.com/javase/8/docs/api/java/util/Random.html
            int num = ran.nextInt(5) + 1;
            log.info(String.format("本轮点数是%d",num));


        }
    }
}
