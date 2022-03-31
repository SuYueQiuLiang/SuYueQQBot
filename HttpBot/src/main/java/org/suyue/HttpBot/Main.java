package org.suyue.HttpBot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import org.suyue.bot.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main implements SuYueBotMod {
    private static final String modName = "HttpBot";
    private JSONArray config;
    private final Map<String,HttpTask> tasks = new HashMap<>();
    private final ModConfig modConfig;
    public Main(){
        modConfig = new ModConfig(modName);
        config = JSON.parseArray(modConfig.readConfig());
        if(config == null)
            return;
        for(int i = 0;i<config.size();i++){
            //int hourOfDay, Bot bot, String taskName, long userQQ, String httpUrl, Map<String, String> headers, String param
            JSONArray headersJson = config.getJSONObject(i).getJSONArray("headers");
            Map<String,String> headers = new HashMap<>();
            for(int ii = 0;ii<headersJson.size();ii++)
                headers.put(headersJson.getJSONObject(ii).getString("headerName"),headersJson.getJSONObject(ii).getString("headerValue"));
            HttpTask task  = new HttpTask(config.getJSONObject(i).getInteger("scheduledTask"), Config.defaultBot,config.getJSONObject(i).getString("taskName"),config.getJSONObject(i).getLong("userQQ"),config.getJSONObject(i).getString("httpUrl"),headers,config.getJSONObject(i).getString("param"));
            tasks.put(config.getJSONObject(i).getString("taskName"),task);
        }
        saveConfig();
    }
    public void saveConfig(){
        config = new JSONArray();
        for(Map.Entry<String,HttpTask> task : tasks.entrySet()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("scheduledTask",task.getValue().hourOfDay);
            jsonObject.put("taskName",task.getValue().taskName);
            jsonObject.put("userQQ",task.getValue().userQQ);
            jsonObject.put("httpUrl",task.getValue().httpUrl);
            jsonObject.put("param",task.getValue().param);
            JSONArray headers = new JSONArray();
            for(Map.Entry<String,String> header : task.getValue().headers.entrySet()){
                JSONObject headerOne = new JSONObject();
                headerOne.put("headerName",header.getKey());
                headerOne.put("headerValue",header.getValue());
                headers.add(headerOne);
            }
            jsonObject.put("headers",headers);
            config.add(jsonObject);
        }
        modConfig.saveConfig(config.toString());
    }
    @Override
    public void receiveMessage(MessageEvent event) {

    }


    @Override
    public void receiveFriendMessage(FriendMessageEvent event) {
        long senderId = event.getSender().getId();
        MessageChain messageChain = event.getMessage();
        String messageStr = messageChain.contentToString();
        if(!Perms.isAdministrator(senderId))
            return;
        if(messageStr.startsWith("listHttpTask")){
            ArrayList<HttpTask> myTasks = new ArrayList<>();
            StringBuilder builder = new StringBuilder();
            builder.append("您有以下Http自动化任务：\n");
            for(Map.Entry<String,HttpTask> task : tasks.entrySet())
                if(Perms.getPerms(task.getValue().userQQ) == Target.owner)
                    builder.append(task.getValue().userQQ).append(" 发起的 ").append(task.getValue().taskName).append(" 执行时间：").append(task.getValue().hourOfDay).append(" Url:").append(task.getValue().httpUrl).append(" 头部信息：").append(task.getValue().headers).append(" 正文信息：").append(task.getValue().param).append("\n");
                else if(task.getValue().userQQ == event.getSender().getId())
                    builder.append(task.getValue().taskName).append(" 执行时间：").append(task.getValue().hourOfDay).append(" Url:").append(task.getValue().httpUrl).append(" 头部信息：").append(task.getValue().headers).append(" 正文信息：").append(task.getValue().param).append("\n");
            event.getSender().sendMessage(builder.toString());
        }else if(messageStr.startsWith("setHttpTask")) {
            if (isMessageOnlyPlainText(messageChain)) {
                String[] split = messageStr.split(" ");
                if (split.length < 5)
                    event.getSender().sendMessage("请求格式错误");
                else {
                    //int hourOfDay, Bot bot, String taskName, long userQQ, String httpUrl, Map<String, String> headers, String param
                    //Message setTask taskName taskTime taskUrl taskHeaders &administrator *NoAt /编辑Get任务
                    try {
                        if(tasks.get(split[1])==null){
                            Map<String,String> headers = jsonToMap(split[4]);
                            HttpTask httpTask;
                            if(split.length == 5)
                                httpTask = new HttpTask(Integer.parseInt(split[2]),Config.defaultBot, split[1],event.getSender().getId(),split[3],headers,"");
                            else
                                httpTask = new HttpTask(Integer.parseInt(split[2]),Config.defaultBot, split[1],event.getSender().getId(),split[3],headers,split[5]);
                            tasks.put(split[1],httpTask);
                            event.getSender().sendMessage("添加任务成功");
                            saveConfig();
                        }else if(tasks.get(split[1]).userQQ == event.getSender().getId()||Perms.getPerms(event.getSender().getId()) == Target.owner){
                            Map<String,String> headers = jsonToMap(split[4]);
                            tasks.get(split[1]).hourOfDay = Integer.parseInt(split[2]);
                            tasks.get(split[1]).taskName = split[1];
                            tasks.get(split[1]).httpUrl = split[3];
                            tasks.get(split[1]).headers = jsonToMap(split[4]);
                            if(split.length == 5)
                                tasks.get(split[1]).param = "";
                            else tasks.get(split[1]).param = split[5];
                            tasks.get(split[1]).flushTask();
                            event.getSender().sendMessage("修改任务成功");
                            saveConfig();
                        }
                    }catch (NumberFormatException | JSONException e){
                        e.printStackTrace();
                        event.getSender().sendMessage("输入格式错误，taskTime请使用0-23正整数,headers请提供JsonArray，JsonArray中仅含有JSONObject，每个JSONObject有两个个String值的\"headerName\"和\"headerValue\"参数");
                    }
                }
            }
        }else if(messageStr.startsWith("removeHttpTask")) {
            if (isMessageOnlyPlainText(messageChain)) {
                String[] split = messageStr.split(" ");
                if (split.length < 2)
                    event.getSender().sendMessage("请求格式错误");
                else if(tasks.get(split[1])!=null){
                    if(Perms.getPerms(event.getSender().getId()) == Target.owner||event.getSender().getId() == tasks.get(split[1]).userQQ){
                        tasks.get(split[1]).task.cancel();
                        tasks.remove(split[1]);
                        event.getSender().sendMessage("删除成功！");
                    }else event.getSender().sendMessage("权限不足！");
                }else event.getSender().sendMessage("目标任务不存在");
            }
        }
    }
    private Map<String,String> jsonToMap(String json){
        JSONArray jsonArray = JSON.parseArray(json);
        Map<String,String> headers = new HashMap<>();
        for(int i = 0;i< jsonArray.size();i++)
            headers.put(jsonArray.getJSONObject(i).getString("headerName"),jsonArray.getJSONObject(i).getString("headerValue"));
        return headers;
    }
    @Override
    public void receiveGroupMessage(GroupMessageEvent event) {

    }


    private static boolean isMessageOnlyPlainText(MessageChain messageChain){
        for(int i =1;i< messageChain.size();i++)
            if(!(messageChain.get(i) instanceof PlainText))
                return false;
        return true;
    }
}
