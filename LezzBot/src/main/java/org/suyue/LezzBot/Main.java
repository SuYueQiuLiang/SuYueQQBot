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

import java.util.HashMap;
import java.util.Map;

public class Main implements SuYueBotMod {
    private static final String modName = "LezzBot";
    private JSONArray config;
    public static int insideVersion;
    public static String baiduMapAk;
    private final Map<Long,LezzTask> users = new HashMap<>();
    private final ModConfig modConfig;

    @Override
    public void unloadMod() {
        for(Map.Entry<Long,LezzTask> entry:users.entrySet()){
            entry.getValue().timer.cancel();
        }
    }

    public Main(){
        modConfig = new ModConfig(modName);
        JSONObject jsonObject = JSON.parseObject(modConfig.readConfig());
        insideVersion = jsonObject.getInteger("insideVersion");
        baiduMapAk = jsonObject.getString("baiduMapAk");
        config = jsonObject.getJSONArray("array");
        if(config == null)
            return;
        for(int i = 0;i<config.size();i++){
            LezzTask lezzTask  = new LezzTask(config.getJSONObject(i).getInteger("scheduledTask"), Config.defaultBot,config.getJSONObject(i).getLong("userQQ"),config.getJSONObject(i).getString("userId"),config.getJSONObject(i).getString("userPassword"),config.getJSONObject(i).getDouble("validMileage"));
            users.put(config.getJSONObject(i).getLong("userQQ"),lezzTask);
        }
    }
    public void saveConfig(){
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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("array",config);
        jsonObject.put("baiduMapAk",baiduMapAk);
        jsonObject.put("insideVersion",insideVersion);
        modConfig.saveConfig(jsonObject.toJSONString());
    }
    @Override
    public void receiveFriendMessage(FriendMessageEvent event) {
        long senderId = event.getSender().getId();
        MessageChain messageChain = event.getMessage();
        String messageStr = messageChain.contentToString();
        String[] split = messageStr.split(" ");
        if(messageStr.startsWith("setLezz")&&split[0].equals("setLezz")){
            if(isMessageOnlyPlainText(messageChain)){
                if(split.length<3)
                    event.getSender().sendMessage("??????????????????");
                else {
                    if(users.get(event.getSender().getId())!=null){
                        users.get(event.getSender().getId()).userId = split[1];
                        users.get(event.getSender().getId()).userPassword = split[2];
                        users.get(event.getSender().getId()).flushTask();
                        event.getSender().sendMessage("??????????????????");
                        saveConfig();
                    }else {
                        LezzTask lezzTask  = new LezzTask(-1,Config.defaultBot, event.getSender().getId(),split[1],split[2],2);
                        users.put(event.getSender().getId(),lezzTask);
                        event.getSender().sendMessage("??????????????????");
                        saveConfig();
                    }
                }
            }else event.getSender().sendMessage("??????????????????");
        }else if(messageStr.startsWith("setLezzTask")&&split[0].equals("setLezzTask")){
            if(isMessageOnlyPlainText(messageChain)){
                if(split.length<3)
                    event.getSender().sendMessage("??????????????????");
                else {
                    if(users.get(event.getSender().getId())!=null){
                        try{
                            users.get(event.getSender().getId()).hourOfDay = Integer.parseInt(split[1]);
                            users.get(event.getSender().getId()).validMileage = Double.parseDouble(split[2]);
                            users.get(event.getSender().getId()).flushTask();
                            event.getSender().sendMessage("????????????????????????");
                            saveConfig();
                        }catch (NumberFormatException e){
                            e.printStackTrace();
                            event.getSender().sendMessage("?????????????????????????????????1??????????????? ??????2??????????????????");
                        }
                    }else
                        event.getSender().sendMessage("????????????????????????");
                }
            }else event.getSender().sendMessage("??????????????????");
        }else if(messageStr.startsWith("runLezz")){
            if(isMessageOnlyPlainText(messageChain)){
                if(split.length<2)
                    event.getSender().sendMessage("??????????????????");
                else {
                    if(users.get(event.getSender().getId())!=null){
                        try{
                            event.getSender().sendMessage("???????????????????????????????????????????????????");
                            Running.run(new HttpUtil(),Config.defaultBot,event.getSender().getId(),users.get(event.getSender().getId()).userId,users.get(event.getSender().getId()).userPassword,Double.parseDouble(split[1]),0);
                        }catch (NumberFormatException e){
                            e.printStackTrace();
                            event.getSender().sendMessage("????????????????????????????????????");
                        }
                    }else
                        event.getSender().sendMessage("????????????????????????");
                }
            }else event.getSender().sendMessage("??????????????????");
        }else if(messageStr.startsWith("getLezzSet")){
            if(users.get(event.getSender().getId())!=null){
                event.getSender().sendMessage("??????????????????"+users.get(event.getSender().getId()).userId+"\n???????????????"+users.get(event.getSender().getId()).userPassword+"\n??????????????????????????????-1???????????????????????????"+users.get(event.getSender().getId()).hourOfDay+"\n????????????????????????????????????"+users.get(event.getSender().getId()).validMileage);
            }else event.getSender().sendMessage("??????????????????????????????");
        }else if(messageStr.startsWith("updateInsideVersion")){
            if(users.get(event.getSender().getId())!=null&&Perms.getPerms(event.getSender().getId()) == Target.owner){
                int i = Running.updateInsideVersion(new HttpUtil(),Config.defaultBot,event.getSender().getId(),users.get(event.getSender().getId()).userId,users.get(event.getSender().getId()).userPassword);
                if(i!=0)
                    saveConfig();
            }else event.getSender().sendMessage("??????????????????????????????");
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
                    event.getGroup().sendMessage(new At(event.getSender().getId()).plus("??????????????????"));
                else {
                    if(users.get(event.getSender().getId())!=null){
                        try{
                            event.getGroup().sendMessage(new At(event.getSender().getId()).plus("?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????"));
                            Running.run(new HttpUtil(),Config.defaultBot,event.getSender().getId(),users.get(event.getSender().getId()).userId,users.get(event.getSender().getId()).userPassword,Double.parseDouble(split[1]),0);
                        }catch (NumberFormatException e){
                            e.printStackTrace();
                            event.getGroup().sendMessage(new At(event.getSender().getId()).plus("????????????????????????????????????"));
                        }
                    }else
                        event.getGroup().sendMessage(new At(event.getSender().getId()).plus("????????????????????????"));
                }
            }else event.getGroup().sendMessage(new At(event.getSender().getId()).plus("??????????????????"));
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
