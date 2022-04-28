package org.suyue.bot;

import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Friend;

import java.io.IOException;
import java.util.ArrayList;

public class CommandHandler {
    public static void handleCommand(String command) throws InterruptedException, IOException {
        String[] splitCommand = command.split(" ");
        switch(splitCommand[0]){
            case "login":
                if(splitCommand.length>=2&&(Config.bots.get(splitCommand[1])!=null)){
                    System.out.println("开始尝试登录Bot "+splitCommand[1]);
                    Thread.sleep(1000);
                    Config.bots.get(splitCommand[1]).login();
                    Config.eventRegister(Config.bots.get(splitCommand[1]));
                }else System.out.println("命令格式错误或无对应Bot对象");
                break;
            case "reloadMod":
                LoadMods.loadMods("./mods/");
                break;
            case "loadMod":
                if(splitCommand.length>=2){
                    LoadMods.loadNewMod("./mods/",splitCommand[1]);
                }else System.out.println("命令格式错误");
                break;
            case "say":
                ContactList<Friend> friends = Config.defaultBot.getFriends();
                for(Friend friend:friends){
                    friend.sendMessage(splitCommand[1]);
                }
                break;
        }
    }
}
