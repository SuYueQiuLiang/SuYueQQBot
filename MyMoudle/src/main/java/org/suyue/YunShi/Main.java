package org.suyue.YunShi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import org.suyue.bot.ModConfig;
import org.suyue.bot.SuYueBotMod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main implements SuYueBotMod {
    private HttpUtil httpUtil;
    private ArrayList<String> word;
    private final Map<Long,YiYanTask> users = new HashMap<>();
    private final ModConfig modConfig;
    private JSONArray config;
    public Main(){
        httpUtil = new HttpUtil();
        word = new ArrayList<>();
        word.add("运势");
        word.add("我的运势");
        word.add("今日运势");
        word.add("抽签");
        word.add("抽");
        modConfig = new ModConfig("YunShi");
        JSONObject jsonObject = JSON.parseObject(modConfig.readConfig());
        config = jsonObject.getJSONArray("array");
        if(config == null)
            return;

    }
    @Override
    public void receiveFriendMessage(FriendMessageEvent event) {
        if(word.contains(event.getMessage().contentToString())){
            event.getSender().sendMessage(draw(event.getSender().getId()));
        }else if(event.getMessage().contentToString().equals("解签")){
            event.getSender().sendMessage(unSign(event.getSender().getId()));
        }
    }

    @Override
    public void receiveGroupMessage(GroupMessageEvent event) {
        if(word.contains(event.getMessage().contentToString())){
            event.getGroup().sendMessage(new At(event.getSender().getId()).plus("\n")
                    .plus(draw(event.getSender().getId())));
        }else if(event.getMessage().contentToString().equals("解签")){
            event.getGroup().sendMessage(new At(event.getSender().getId()).plus("\n")
                    .plus(unSign(event.getSender().getId())));
        }
    }

    @Override
    public void receiveMessage(MessageEvent event) {

    }
    public String draw(long qq){
        StringBuilder builder = new StringBuilder();
        String jsonStr = httpUtil.doGet("https://api.fanlisky.cn/api/qr-fortune/get/"+qq,new HashMap<>());
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        if(jsonObject.getInteger("code") != 200)
            return "未能查询到您的今日运势，请稍后再试！";
        jsonObject = jsonObject.getJSONObject("data");
        builder.append("今日运势：").append(jsonObject.getString("fortuneSummary"))
                .append("\n").append("运势星级：").append(jsonObject.getString("luckyStar"))
                .append("\n").append("签词：").append(jsonObject.getString("signText"))
                .append("\n").append("如果需要解签，请输入\"解签\"");
        return builder.toString();
    }
    public String unSign(long qq){
        StringBuilder builder = new StringBuilder();
        String jsonStr = httpUtil.doGet("https://api.fanlisky.cn/api/qr-fortune/get/"+qq,new HashMap<>());
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        if(jsonObject.getInteger("code") != 200)
            return "解签失败，请稍后再试！";
        jsonObject = jsonObject.getJSONObject("data");
        builder.append("签词：").append(jsonObject.getString("signText"))
                .append("\n").append("解签：").append(jsonObject.getString("unSignText"));
        return builder.toString();
    }
}
