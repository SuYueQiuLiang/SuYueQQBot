package org.suyue.LezzBot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import org.suyue.bot.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main implements SuYueBotMod {
    private static final String modName = "LezzBot";
    private JSONArray config;
    private Map<Long,LezzTask> users = new HashMap<>();
    private ModConfig modConfig;
    public Main(){
        modConfig = new ModConfig(modName);
        config = JSON.parseArray(modConfig.readConfig());
        if(config == null)
            return;
        for(int i = 0;i<config.size();i++){
            LezzTask lezzTask  = new LezzTask(config.getJSONObject(i).getInteger("scheduledTask"), Config.defaultBot,config.getJSONObject(i).getLong("userQQ"),config.getJSONObject(i).getString("userId"),config.getJSONObject(i).getString("userPassword"),config.getJSONObject(i).getDouble("validMileage"));;
            users.put(config.getJSONObject(i).getLong("userQQ"),lezzTask);
        }
    }
    private void saveConfig(){
        config = new JSONArray();
        for(Map.Entry<Long,LezzTask> userEntry : users.entrySet()){
            JSONObject object = new JSONObject();
            object.put("userQQ",userEntry.getKey());
            object.put("userId",userEntry.getValue().userId);
            object.put("userPassword",userEntry.getValue().userPassword);
            object.put("scheduledTask",userEntry.getValue().hourOfDay);
            object.put("validMileage",userEntry.getValue().validMileage);
            config.add(object);
        }
        modConfig.saveConfig(config.toString());
    }
    @Override
    public void receiveFriendMessage(FriendMessageEvent event) {
        long senderId = event.getSender().getId();
        MessageChain messageChain = event.getMessage();
        String messageStr = messageChain.contentToString();
        if(messageStr.startsWith("setLezz")){
            if(isMessageOnlyPlainText(messageChain)){
                String[] split = messageStr.split(" ");
                if(split.length<3)
                    event.getSender().sendMessage("请求格式错误");
                else {
                    if(users.get(event.getSender().getId())!=null){
                        users.get(event.getSender().getId()).userId = split[1];
                        users.get(event.getSender().getId()).userPassword = split[2];
                        users.get(event.getSender().getId()).flushTask();
                        event.getSender().sendMessage("修改账号成功");
                        saveConfig();
                    }else {
                        LezzTask lezzTask  = new LezzTask(-1,Config.defaultBot, event.getSender().getId(),split[1],split[2],2);
                        users.put(event.getSender().getId(),lezzTask);
                        event.getSender().sendMessage("绑定账号成功");
                        saveConfig();
                    }
                }
            }else event.getSender().sendMessage("携带非法资源");
        }else if(messageStr.startsWith("setTask")){
            if(isMessageOnlyPlainText(messageChain)){
                String[] split = messageStr.split(" ");
                if(split.length<3)
                    event.getSender().sendMessage("请求格式错误");
                else {
                    if(users.get(event.getSender().getId())!=null){
                        try{
                            users.get(event.getSender().getId()).hourOfDay = Integer.parseInt(split[1]);
                            users.get(event.getSender().getId()).validMileage = Double.parseDouble(split[2]);
                            users.get(event.getSender().getId()).flushTask();
                            event.getSender().sendMessage("修改计划任务成功");
                            saveConfig();
                        }catch (NumberFormatException e){
                            e.printStackTrace();
                            event.getSender().sendMessage("参数格式错误，请在参数1使用整数， 参数2使用任意数字");
                        }
                    }else
                        event.getSender().sendMessage("请先设置乐健账号");
                }
            }else event.getSender().sendMessage("携带非法资源");
        }else if(messageStr.startsWith("runLezz")){
            if(isMessageOnlyPlainText(messageChain)){
                String[] split = messageStr.split(" ");
                if(split.length<2)
                    event.getSender().sendMessage("请求格式错误");
                else {
                    if(users.get(event.getSender().getId())!=null){
                        try{
                            Running.run(new HttpUtil(),Config.defaultBot,event.getSender().getId(),users.get(event.getSender().getId()).userId,users.get(event.getSender().getId()).userPassword,Double.parseDouble(split[1]));
                            event.getSender().sendMessage("已发送请求，请稍后在私聊中查看推送");
                        }catch (NumberFormatException e){
                            e.printStackTrace();
                            event.getSender().sendMessage("参数格式错误，请输入数字");
                        }
                    }else
                        event.getSender().sendMessage("请先设置乐健账号");
                }
            }else event.getSender().sendMessage("携带非法资源");
        }else if(messageStr.startsWith("getLezzSet")){
            if(users.get(event.getSender().getId())!=null){
                event.getSender().sendMessage("乐健用户名："+users.get(event.getSender().getId()).userId+"\n乐健密码："+users.get(event.getSender().getId()).userPassword+"\n设定的任务定时时间（-1为关闭定时任务）："+users.get(event.getSender().getId()).hourOfDay+"\n定时任务设定的目标里程："+users.get(event.getSender().getId()).validMileage);
            }else event.getSender().sendMessage("你还没有绑定乐健账号");
        }
    }
    @Override
    public void receiveGroupMessage(GroupMessageEvent event) {
        MessageChain messageChain = event.getMessage();
        String messageStr = messageChain.contentToString();
        if(messageStr.startsWith("runLezz")){
            if(isMessageOnlyPlainText(messageChain)){
                String[] split = messageStr.split(" ");
                if(split.length<2)
                    event.getGroup().sendMessage(new At(event.getSender().getId()).plus("请求格式错误"));
                else {
                    if(users.get(event.getSender().getId())!=null){
                        try{
                            Running.run(new HttpUtil(),Config.defaultBot,event.getSender().getId(),users.get(event.getSender().getId()).userId,users.get(event.getSender().getId()).userPassword,Double.parseDouble(split[1]));
                            event.getGroup().sendMessage(new At(event.getSender().getId()).plus("已发送请求，请稍后在私聊中查看推送，如果未推送并且需要推送，请添加机器人为好友"));
                        }catch (NumberFormatException e){
                            e.printStackTrace();
                            event.getGroup().sendMessage(new At(event.getSender().getId()).plus("参数格式错误，请输入整数"));
                        }
                    }else
                        event.getGroup().sendMessage(new At(event.getSender().getId()).plus("请先设置乐健账号"));
                }
            }else event.getGroup().sendMessage(new At(event.getSender().getId()).plus("携带非法资源"));
        }
    }

    @Override
    public void receiveMessage(MessageEvent event) {

    }

    private static boolean isMessageOnlyPlainText(MessageChain messageChain){
        for(int i =1;i< messageChain.size();i++)
            if(!(messageChain.get(i) instanceof PlainText))
                return false;
        return true;
    }
}
