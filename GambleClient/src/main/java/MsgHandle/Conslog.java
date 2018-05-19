package MsgHandle;

public class Conslog {
    public static void UserQiute(String name){
        System.out.println(String.format("%s 悄悄的走了，不带走一个筹码", name));
    }

    public static void UserLose(String name){
        System.out.println(String.format("%s 输个精光，被一脚踢出！", name));
    }
}
