package org.suyue.LezzBot;

import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import org.suyue.bot.FileUtils;

import java.io.IOException;

public class Running {
    private static final String host = "https://cpes.legym.cn";
    public static void main(String[] args){
        run(new HttpUtil(),null, 1569938823L,"19828436143","Shuruiting200012",3d);
    }
    public static void run(HttpUtil httpUtil,Bot bot, long userQQ, String userId, String userPassword,double validMileage){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("entrance","1");
            jsonObject.put("password",userPassword);
            jsonObject.put("userName",userId);
            httpUtil.doGet(host + "/education/semester/getCurrent",null);
            String info = httpUtil.doPost(host+"/authorization/user/manage/login", jsonObject.toJSONString(), null);
            //对http进行分析，解析收到数据code是否为0
            if(info == null){
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("LezzBot登录失败！请检查用户名密码！");
                return;
            }
            jsonObject = JSONObject.parseObject(info);
            if(jsonObject.getInteger("code")!=0){
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("LezzBot登录失败！请检查用户名密码！");
                return;
            }
            //如果收到数据并且code为正常，解析包内数据并且写入userInfo
            jsonObject = jsonObject.getJSONObject("data");
            String token = jsonObject.getString("accessToken"),organizationUserNumber = jsonObject.getString("organizationUserNumber"),organizationName = jsonObject.getString("schoolName") + " " +jsonObject.getString("organizationName");
            String realName = jsonObject.getString("realName");
            JSONObject userInfo = new JSONObject();
            userInfo.put("organizationUserNumber",organizationUserNumber);
            userInfo.put("organizationName",organizationName);
            userInfo.put("realName",realName);
            info = httpUtil.doGet(host+"/authorization/mobileApp/getLastVersion?platform=2", token);
            if(info == null){
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("获取版本信息失败！无法确保安全，停止任务！");
                return;
            }
            jsonObject = JSONObject.parseObject(info);
            if(jsonObject.getInteger("code")!=0){
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("获取版本信息失败！无法确保安全，停止任务！");
                return;
            }
            if(jsonObject.getJSONObject("data").getInteger("version")!=Main.insideVersion){
                System.err.println("乐健已更新新版本："+jsonObject.getJSONObject("data").getInteger("version"));
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("乐健更新新版本！无法确保安全，停止任务！");
                return;
            }
            //请求用户具体数据，并且解析其中的总共已通过里程
            info = httpUtil.doGet(host+"/running/app/getHistoryDetails",token);
            //对http进行分析，解析收到数据code是否为0，如果不正常，此处可以返回登陆时已经获取到的数据
            if(info == null){
                saveUserInfo(userInfo,userQQ,userId,userPassword);
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("获取用户详细数据失败！仍在尝试跑步");
            }
            jsonObject = JSONObject.parseObject(info);
            if(jsonObject.getInteger("code")!=0){
                saveUserInfo(userInfo,userQQ,userId,userPassword);
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("获取用户详细数据失败！仍在尝试跑步");
            }
            //如果数据无误，写入userInfo
            userInfo.put("totalMileage",jsonObject.getJSONObject("data").getString("totalMileage"));
            info = httpUtil.doGet(host+"/education/semester/getCurrent",token);
            //对http进行分析，解析收到数据code是否为0，如果不正常，此处可以返回登陆时已经获取到的数据
            if(info == null){
                saveUserInfo(userInfo,userQQ,userId,userPassword);
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("获取semesterId失败，结束流程");
                return;
            }
            jsonObject = JSONObject.parseObject(info);
            if(jsonObject.getInteger("code")!=0){
                saveUserInfo(userInfo,userQQ,userId,userPassword);
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("获取semesterId失败，结束流程");
                return;
            }
            String semesterId = jsonObject.getJSONObject("data").getString("id");
            JSONObject semJSON = new JSONObject();
            semJSON.put("semesterId",semesterId);
            info = httpUtil.doPost(host + "/running/app/getRunningLimit",semJSON.toJSONString(),token);
            if(info == null){
                saveUserInfo(userInfo,userQQ,userId,userPassword);
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("获取limitationsGoalsSexInfoId失败，结束流程");
                return;
            }
            jsonObject = JSONObject.parseObject(info);
            if(jsonObject.getInteger("code")!=0){
                saveUserInfo(userInfo,userQQ,userId,userPassword);
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("获取limitationsGoalsSexInfoId失败，结束流程");
                return;
            }
            String limitationsGoalsSexInfoId = jsonObject.getJSONObject("data").getString("limitationsGoalsSexInfoId");
            String endJsonReturn = httpUtil.doPost(host + "/running/app/uploadRunningDetails",GenerateClass.getRunningDetail(semesterId,limitationsGoalsSexInfoId,validMileage),token);
            if(endJsonReturn == null){
                saveUserInfo(userInfo,userQQ,userId,userPassword);
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("发送跑步数据包失败！");
                return;
            }
            jsonObject = JSONObject.parseObject(endJsonReturn);
            if(jsonObject.getInteger("code")!=0){
                saveUserInfo(userInfo,userQQ,userId,userPassword);
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("发送跑步数据包失败！");
                return;
            }
            Friend friend = bot.getFriend(userQQ);
            saveUserInfo(userInfo,userQQ,userId,userPassword);
            if(friend!=null)
                friend.sendMessage("跑步成功！");
        }catch (Exception e){
            e.printStackTrace();
            Friend friend = bot.getFriend(userQQ);
            if(friend!=null)
                friend.sendMessage("跑步失败！意料之外的错误！");
        }
    }
    public static int updateInsideVersion(HttpUtil httpUtil,Bot bot, long userQQ, String userId, String userPassword){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("entrance","1");
            jsonObject.put("password",userPassword);
            jsonObject.put("userName",userId);
            httpUtil.doGet(host + "/education/semester/getCurrent",null);
            String info = httpUtil.doPost(host+"/authorization/user/manage/login", jsonObject.toJSONString(), null);
            //对http进行分析，解析收到数据code是否为0
            if(info == null){
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("LezzBot登录失败！请检查用户名密码！");
                return 0;
            }
            jsonObject = JSONObject.parseObject(info);
            if(jsonObject.getInteger("code")!=0){
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("LezzBot登录失败！请检查用户名密码！");
                return 0;
            }
            //如果收到数据并且code为正常，解析包内数据并且写入userInfo
            jsonObject = jsonObject.getJSONObject("data");
            String token = jsonObject.getString("accessToken"),organizationUserNumber = jsonObject.getString("organizationUserNumber"),organizationName = jsonObject.getString("schoolName") + " " +jsonObject.getString("organizationName");
            String realName = jsonObject.getString("realName");
            JSONObject userInfo = new JSONObject();
            userInfo.put("organizationUserNumber",organizationUserNumber);
            userInfo.put("organizationName",organizationName);
            userInfo.put("realName",realName);
            info = httpUtil.doGet(host+"/authorization/mobileApp/getLastVersion?platform=2", token);
            if(info == null){
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("获取版本信息失败！");
                return 0;
            }
            jsonObject = JSONObject.parseObject(info);
            if(jsonObject.getInteger("code")!=0){
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("获取版本信息失败！");
                return 0;
            }
            if(jsonObject.getJSONObject("data").getInteger("version")!=Main.insideVersion){
                Main.insideVersion = jsonObject.getJSONObject("data").getInteger("version");
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("新的版本号："+jsonObject.getJSONObject("data").getInteger("version"));
                return jsonObject.getJSONObject("data").getInteger("version");
            }else {
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("版本号未更新！");
                return 0;
            }
        }catch (Exception e){
            e.printStackTrace();
            Friend friend = bot.getFriend(userQQ);
            if(friend!=null)
                friend.sendMessage("跑步失败！意料之外的错误！");
            return 0;
        }
    }
    private static void saveUserInfo(JSONObject userInfo,long userQQ,String userId,String userPassword){
        try {
            userInfo.put("userQQ",userQQ);
            userInfo.put("userID",userId);
            userInfo.put("userPassword",userPassword);
            FileUtils.saveFileWithString(userInfo.toJSONString(),"./mods/LezzBot/"+userQQ+".json");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("保存 "+userQQ+".json 失败");
        }
    }
}
