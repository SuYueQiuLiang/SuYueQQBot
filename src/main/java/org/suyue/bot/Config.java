package org.suyue.bot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.utils.BotConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Config {
    public static Map<String,Bot> bots = new HashMap<>();
    static String cache = "./mirai/";
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
            Bot bot = BotFactory.INSTANCE.newBot(botJson.getJSONObject(i).getInteger("botUserName"), botJson.getJSONObject(i).getString("botPassword"),botConfiguration);
            if(botJson.getJSONObject(i).getBoolean("autoLogin")){
                bot.login();
                eventRegister(bot);
            }
            bots.put(botJson.getJSONObject(i).getString("botName"),bot);
            System.out.println("成功载入Bot " + botJson.getJSONObject(i).getString("botName"));
        }
        LoadMods.loadMods("./mods/");
        return true;
    }
    public static void eventRegister(Bot bot) {
        bot.getEventChannel().subscribeAlways(GroupMessageEvent.class, (event) -> {
            for(Map.Entry<String,SuYueBotMod> entry: LoadMods.mods.entrySet()){
                entry.getValue().receiveGroupMessage(event.getGroup().getId(),event.getSender().getId(),event.getMessage());
            }
        });
        bot.getEventChannel().subscribeAlways(MessageEvent.class,(event)->{
            for(Map.Entry<String,SuYueBotMod> entry: LoadMods.mods.entrySet()){
                entry.getValue().receiveMessage(event.getSender().getId(),event.getMessage());
            }
        });
    }
}
