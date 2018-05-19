package Game;

import Client.Client;
import Client.MsgTool;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Game implements Runnable {
    Thread thread;
    ConcurrentHashMap<String, Client> Clients;
    ConcurrentHashMap<String, Gambler> Gamblers;
    Random ran;
    MsgTool msgTool;
    private static String name = Game.class.getName();
    private static Logger log = Logger.getLogger(name);
    private static int serverChips = 5000;

    public Game(ConcurrentHashMap<String, Client> Clients) {
        this.Clients = Clients;
        Gamblers = new ConcurrentHashMap<>();
        // default use System.nanoTime()
        ran = new Random();
        msgTool = new MsgTool(Clients);
    }

    public Thread Start () {
        thread = new Thread (this);
        thread.start();
        return thread;
    }

    /**
     * Make A client join in the game
     * @param client is the user wait join
     * @param chips is the chip, that already been deducted from client.Chips!!
     * @param betType true represent Big, false represent Small
     * @return if already join, return false
     */
    public boolean Join(Client client, int chips, boolean betType){
        if (!Gamblers.containsKey(client.Name)) {
            Gamblers.put(name, new Gambler(client, chips, betType?BetType.Big:BetType.Small));
            return true;
        }
        else return false;
    }


    @Override
    public void run() {
        while (true) {

            msgTool.Boardcast("GamblePrepareNotify", "开始啦！大家快下注啦！赌大小啊！翻倍赢啊");

            // Delay
            try {
                //See more detail at http://www.importnew.com/7219.html
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                return;
            }

            msgTool.Boardcast("GambleStartNotify", "停止下注啦！都不要动啦！马上要开啦！开！开！开！。");

            // 锁定本局参与者
            ConcurrentHashMap<String, Gambler> nowGamblers;
            synchronized (Gamblers) {
                nowGamblers = Gamblers;
                //TODO:先创建再直接赋值？
                Gamblers = new ConcurrentHashMap<>();
            }

            // About uniformly distributed see more about https://stackoverflow.com/questions/20389890/generating-a-random-number-between-1-and-10-java
            // and https://docs.oracle.com/javase/8/docs/api/java/util/Random.html
            int num = ran.nextInt(5) + 1;

            msgTool.Boardcast("GambleNumNotify", String.format("%d",num));

            BetType betType;
            log.info(String.format("本轮点数是%d",num));
            if (num <4){
                betType = BetType.Small;
            } else {
                betType = BetType.Big;
            }

            for (Gambler g:nowGamblers.values()){
                if (g.betType == betType){
                    serverChips -= g.Win();
                } else {
                    serverChips += g.Lose();
                }
            }

            if (serverChips < 0){
                msgTool.BoardcastLastMsg("GambleServerChipEmptyNotify", "庄家运气怎么这么差，竟然输光了，掀桌子不玩儿了！大家散场啦！");
                return;
            }
        }
    }
}
