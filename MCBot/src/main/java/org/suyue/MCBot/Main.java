package org.suyue.MCBot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import org.suyue.bot.FileUtils;
import org.suyue.bot.ModConfig;
import org.suyue.bot.SuYueBotMod;
import net.kronos.rkon.core.Rcon;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Main implements SuYueBotMod {
    Map<Long,Rcon> rconMap = new HashMap<>();
    public Main(){


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
