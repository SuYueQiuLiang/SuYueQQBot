package org.suyue.MCBot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
    public String receiveGroupMessage(long groupId,long userId, MessageChain messages) {

        return null;
    }

    @Override
    public String receiveMessage(long userId, MessageChain messages) {
        return null;
    }
}
