package org.suyue.bot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Perms {
    private static final Map<Long,Target> perMap = new HashMap<>();
    public static boolean isAdministrator(Long userId) {
        if(perMap.get(userId)==null)
            return false;
        return perMap.get(userId) == Target.administrator || perMap.get(userId) == Target.owner;
    }
    public static void loadPerms() throws IOException {
        JSONArray jsonArray = JSON.parseArray(FileUtils.readFileToString("./perms.cfg"));
        for(int i = 0;i< jsonArray.size();i++){
            perMap.put(jsonArray.getJSONObject(i).getLong("userId"),Target.values()[jsonArray.getJSONObject(i).getInteger("perms")]);
        }
    }
    public static Map<Long,Target> listPerms(){
        return perMap;
    }
    public static Target getPerms(Long userId){
        return perMap.get(userId) == null ? Target.user : perMap.get(userId);
    }
    public static void removePerms(Long userId) throws IOException {
        if(perMap.get(userId)!=null)
            perMap.remove(userId);
        savePerms();
    }
    public static void addPerms(Long userId,Target target) throws IOException {
        if(perMap.get(userId)!=null)
            removePerms(userId);
        perMap.put(userId, target);
        savePerms();
    }
    public static void savePerms() throws IOException {
        JSONArray jsonArray = new JSONArray();
        for(Map.Entry<Long,Target> entry : perMap.entrySet()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId",entry.getKey());
            jsonObject.put("perms",entry.getValue().ordinal());
            jsonArray.add(jsonObject);
        }
        FileUtils.saveFileWithString(jsonArray.toString(),"./perms.cfg");
    }
}
