import Client.Client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser{
    Client client;

    public CommandParser(Client client){
        this.client = client;
    }

    public void Parase(String cmd){

        String pattern = "^(\\d+) ([D|X])$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(cmd);
        if (m.find()){
            int num = Integer.parseInt(m.group(1));
            if (m.group(2).equals("D")){
                boolean betType = true;
            } else {
                boolean betType = false;
            }
        } else {
            System.out.println(String.format("你说啥？要按套路出牌哦！您有%s个筹码，请下注：", client.Chips));
        }

    }

}
