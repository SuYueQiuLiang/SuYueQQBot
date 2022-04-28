package org.suyue.bot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.BotConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Config {
    public static Map<String,Bot> bots = new HashMap<>();
    static String cache = "./mirai/";
    public static Bot defaultBot;
    static BotConfiguration.MiraiProtocol miraiProtocol = BotConfiguration.MiraiProtocol.ANDROID_PHONE;
    public static Map<String,Bot> listBot(){
        return bots;
    }
    public static boolean loadConfig() throws IOException{
        JSONObject jsonObject = JSON.parseObject(FileUtils.readFileToString("./config.cfg"));
        JSONArray botJson = jsonObject.getJSONArray("bots");
        for(int i = 0;i<botJson.size();i++){
            File cacheFile = new File(cache + botJson.getJSONObject(i).getString("botName")+"/");
            BotConfiguration botConfiguration = new BotConfiguration() {{
                setWorkingDir(cacheFile);
                fileBasedDeviceInfo();
                setProtocol(miraiProtocol);
            }};
            Bot bot = BotFactory.INSTANCE.newBot(botJson.getJSONObject(i).getLong("botUserName"), botJson.getJSONObject(i).getString("botPassword"),botConfiguration);
            if(botJson.getJSONObject(i).getBoolean("autoLogin")||botJson.getJSONObject(i).getBoolean("default")){
                bot.login();
                eventRegister(bot);
            }
            if(botJson.getJSONObject(i).getBoolean("default"))
                defaultBot = bot;
            bots.put(botJson.getJSONObject(i).getString("botName"),bot);
            System.out.println("成功载入Bot " + botJson.getJSONObject(i).getString("botName"));
        }
        LoadMods.loadMods("./mods/");
        return true;
    }
    public static void eventRegister(Bot bot) {
        bot.getEventChannel().subscribeAlways(NewFriendRequestEvent.class, NewFriendRequestEvent::accept);
        bot.getEventChannel().subscribeAlways(GroupMessageEvent.class, (event) -> {
            MessageChain messageChain = event.getMessage();
            String messageStr = messageChain.contentToString();
            String[] split = messageStr.split(" ");
            if(isMessageOnlyPlainText(messageChain)&&split[0].equals("help")){
                if(split.length == 1)
                    event.getGroup().sendMessage(new At(event.getSender().getId()).plus(getHelpStr()));
                else if(LoadMods.modHelps.get(split[1])!=null)
                    event.getGroup().sendMessage(new At(event.getSender().getId()).plus(LoadMods.modHelps.get(split[1])));
            }else
                for(Map.Entry<String,SuYueBotMod> entry: LoadMods.mods.entrySet()){
                    entry.getValue().receiveGroupMessage(event);
                }
        });
        bot.getEventChannel().subscribeAlways(MessageEvent.class,(event)-> {
            for (Map.Entry<String, SuYueBotMod> entry : LoadMods.mods.entrySet()) {
                entry.getValue().receiveMessage(event);
            }
        });
        bot.getEventChannel().subscribeAlways(FriendMessageEvent.class,(event)->{
            MessageChain messageChain = event.getMessage();
            String messageStr = messageChain.contentToString();
            String[] split = messageStr.split(" ");
            if (isMessageOnlyPlainText(messageChain) && split[0].equals("help")) {
                if (split.length == 1)
                    event.getSender().sendMessage(getHelpStr());
                else if (LoadMods.modHelps.get(split[1]) != null)
                    event.getSender().sendMessage(LoadMods.modHelps.get(split[1]));
            } else
                for (Map.Entry<String, SuYueBotMod> entry : LoadMods.mods.entrySet()) {
                    entry.getValue().receiveFriendMessage(event);
                }
        });
    }
    private static boolean isMessageOnlyPlainText(MessageChain messageChain){
        for(int i =1;i< messageChain.size();i++)
            if(!(messageChain.get(i) instanceof PlainText))
                return false;
        return true;
    }
    private static String getHelpStr(){
        try {
            StringBuilder builder = new StringBuilder(FileUtils.readFileToString("./help.txt"));
            builder.append("\n目前已经加载的Mod：");
            for(Map.Entry<String,SuYueBotMod> entry : LoadMods.mods.entrySet())
                builder.append(entry.getKey()).append(" ");
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "读取Help文件失败！";
        }
    }
}
