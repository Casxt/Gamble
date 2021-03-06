package Game;

import Client.Client;
import Client.MsgTool;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class Game implements Runnable {
    private static String name = Game.class.getName();
    private static Logger log = Logger.getLogger(name);
    private static int serverChips = 5000;
    private ConcurrentHashMap<String, Gambler> Gamblers;
    private Random ran;
    private MsgTool msgTool;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public Game(ConcurrentHashMap<String, Client> Clients) {
        Gamblers = new ConcurrentHashMap<>();
        // by default use System.nanoTime()
        ran = new Random();
        msgTool = new MsgTool(Clients);
    }

    public Thread Start() {
        Thread thread = new Thread(this);
        thread.start();
        return thread;
    }

    /**
     * Make A client join in the game
     *
     * @param client  is the user wait join
     * @param chips   is the chip, that already been deducted from client.Chips!!
     * @param betType true represent Big, false represent Small
     * @return if already join, return false
     */
    public boolean Join(Client client, int chips, boolean betType) {
        // ConcurrentHashMap is thread safe,
        // but need to avoid change during confirm gambler
        lock.readLock().lock();
        if (!Gamblers.containsKey(client.Name)) {

            Gamblers.put(name, new Gambler(client, chips, betType ? BetType.Big : BetType.Small));

            lock.readLock().unlock();
            return true;
        } else {

            lock.readLock().unlock();
            return false;
        }

    }


    @Override
    public void run() {
        ConcurrentHashMap<String, Gambler> nowGamblers, temp;
        while (true) {

            log.info(String.format("庄家剩余点数%d", serverChips));
            msgTool.Broadcast("GamblePrepareNotify", "开始啦！大家快下注啦！赌大小啊！翻倍赢啊");

            // Delay
            try {
                //See more detail at http://www.importnew.com/7219.html
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                return;
            }

            msgTool.Broadcast("GambleStartNotify", "停止下注啦！都不要动啦！马上要开啦！开！开！开！。");

            // confirm gambler of this round of game, create Map outside lock, decrease the time of lock;
            temp = new ConcurrentHashMap<>();

            // Get Write Lock avoid Gamblers change during confirm gambler
            lock.writeLock().lock();
            // ptr exchange should be very fast
            nowGamblers = Gamblers;
            Gamblers = temp;

            lock.writeLock().unlock();

            // About uniformly distributed see more about
            // https://stackoverflow.com/questions/20389890/generating-a-random-number-between-1-and-10-java
            // and https://docs.oracle.com/javase/8/docs/api/java/util/Random.html
            int num = ran.nextInt(5) + 1;

            msgTool.Broadcast("GambleNumNotify", String.format("%d", num));

            BetType betType;
            log.info(String.format("本轮点数是%d", num));
            if (num < 4) {
                betType = BetType.Small;
            } else {
                betType = BetType.Big;
            }

            for (Gambler g : nowGamblers.values()) {

                if (g.betType == betType) {
                    serverChips -= g.Win();
                } else {
                    serverChips += g.Lose();
                }

                if (g.client.Chips <= 0) {
                    g.client.KeepOpen(false);
                    msgTool.Broadcast("GambleUserChipEmptyNotify", String.format("%s输个精光，被一脚踢出！", g.client.Name),
                            "Name", g.client.Name);
                    break;
                }

            }

            if (serverChips <= 0) {
                msgTool.BroadcastLastMsg("GambleServerChipEmptyNotify", "庄家运气怎么这么差，竟然输光了，掀桌子不玩儿了！大家散场啦！");
                return;
            }
        }
    }
}
