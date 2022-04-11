package org.suyue.FeiYanBot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import org.suyue.bot.SuYueBotMod;

import java.util.Timer;

public class Main implements SuYueBotMod {
    private static final long PERIOD_DAY = 30 * 60 * 1000;
    public MyTimeTask task;
    public Timer timer;
    public Main(){
        timer = new Timer();
        task = new MyTimeTask();
        timer.schedule(task, 0, PERIOD_DAY);
    }
    @Override
    public void receiveFriendMessage(FriendMessageEvent event) {
        MessageChain messageChain = event.getMessage();
        String messageStr = messageChain.contentToString();
        String[] split = messageStr.split(" ");
        if (split.length == 2 && split[1].equals("疫情")) {
            event.getSender().sendMessage(getStringWithCity(split[0]));
        }
    }

    @Override
    public void receiveGroupMessage(GroupMessageEvent event) {
        MessageChain messageChain = event.getMessage();
        String messageStr = messageChain.contentToString();
        String[] split = messageStr.split(" ");
        if (split.length == 2 && split[1].equals("疫情")) {
            event.getGroup().sendMessage(new At(event.getSender().getId()).plus(getStringWithCity(split[0])));
        }
    }

    @Override
    public void receiveMessage(MessageEvent event) {

    }
    private String getStringWithCity(String cityName){
        StringBuilder builder = new StringBuilder();
        JSONObject jsonObject = Data.data.getJSONObject("data");
        builder.append(jsonObject.getString("times")).append("\n");
        JSONArray array = jsonObject.getJSONArray("list");
        for(int i = 0;i<array.size();i++){
            if(array.getJSONObject(i).getString("name").replace("市","").replace("省","").equals(cityName)){
                builder.append(cityName).append("现有确诊：").append(array.getJSONObject(i).getString("econNum")).append("\n现有无症状：").append(array.getJSONObject(i).getString("asymptomNum")).append("\n今日新增：").append(array.getJSONObject(i).getString("conadd"));
                return builder.toString();
            }else {
                JSONArray city = array.getJSONObject(i).getJSONArray("city");
                for(int ii = 0;ii<city.size();ii++){
                    if(city.getJSONObject(ii).getString("name").replace("市","").replace("省","").equals(cityName)||city.getJSONObject(ii).getString("mapName").equals(cityName)){
                        builder.append(cityName).append("现有确诊：").append(city.getJSONObject(ii).getString("econNum")).append("\n现有无症状：").append(city.getJSONObject(ii).getString("asymptomNum")).append("\n今日新增：").append(city.getJSONObject(ii).getString("conadd"));
                        return builder.toString();
                    }
                }
            }
        }
        return "未查询到 " + cityName + "的疫情详情";
    }
}
