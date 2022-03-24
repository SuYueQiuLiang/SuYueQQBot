package org.suyue.MCBot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import org.suyue.bot.SuYueBotMod;

import java.io.*;

public class Main implements SuYueBotMod {
    public Main(){

        File file = new File("/mods/MCBot.json");
        if(!file.exists())
            return;
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = bufferedReader.readLine())!=null)
                builder.append(line);
            JSONObject jsonObject = JSON.parseObject(builder.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void receiveGroupMessage(GroupMessageEvent groupMessageEvent) {

    }

    @Override
    public void receiveMessage(MessageEvent messageEvent) {

    }

    @Override
    public void receiveFriendMessage(FriendMessageEvent event) {

    }

}
