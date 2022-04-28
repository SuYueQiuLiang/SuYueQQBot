package org.suyue.LezzBot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import org.suyue.bot.FileUtils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class Running {
    private static final String host = "https://cpes.legym.cn";
    private static final String baiduMapHost = "https://api.map.baidu.com";
    public static void main(String[] args){
        new Main();
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
            String versionLabel = jsonObject.getJSONObject("data").getString("versionLabel");
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
                    friend.sendMessage("获取limitationsGoalsSexInfoId/patternId失败，结束流程");
                return;
            }
            jsonObject = JSONObject.parseObject(info);
            if(jsonObject.getInteger("code")!=0){
                saveUserInfo(userInfo,userQQ,userId,userPassword);
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("获取limitationsGoalsSexInfoId/patternId失败，结束流程");
                return;
            }
            String limitationsGoalsSexInfoId = jsonObject.getJSONObject("data").getString("limitationsGoalsSexInfoId");
            String patternId = jsonObject.getJSONObject("data").getString("patternId");
            //{"latitude":"30.823632269965277","longitude":"106.12070583767361","limitationsGoalsSexInfoId":"402888da7c3a16bb017c3a172da40198","patternId":"8a9780da7c3634e0017c5405f20a0566","scoringType":1,"semesterId":"8a9780647ef79db8017f006a4e700047"}
            DecimalFormat decimalFormat1 = new DecimalFormat("0.0000000000000000");
            DecimalFormat decimalFormat2 = new DecimalFormat("0.000000000000000");
            Random random = new Random();
            jsonObject = new JSONObject();
            jsonObject.put("latitude",28.82794867621528 + Double.parseDouble(decimalFormat1.format((random.nextDouble()-0.5)/1000000)));
            jsonObject.put("longitude",105.11900607638889 + Double.parseDouble(decimalFormat2.format((random.nextDouble()-0.5)/10000)));
            jsonObject.put("limitationsGoalsSexInfoId",limitationsGoalsSexInfoId);
            jsonObject.put("patternId",patternId);
            jsonObject.put("semesterId",semesterId);
            jsonObject.put("scoringType",1);
            String runningRange = httpUtil.doPost(host+"/running/app/getRunningRange",jsonObject.toJSONString(),token);
            if(runningRange == null){
                saveUserInfo(userInfo,userQQ,userId,userPassword);
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("获取跑步范围失败！");
                return;
            }
            jsonObject = JSONObject.parseObject(runningRange);
            if(jsonObject.getInteger("code")!=0){
                saveUserInfo(userInfo,userQQ,userId,userPassword);
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("获取跑步范围失败！");
                return;
            }
            ArrayList<Point> targetPoints;
            jsonObject = jsonObject.getJSONObject("data");
            JSONArray pointArray = jsonObject.getJSONArray("signRunningRegion").getJSONArray(0);
            double maxLa=0,maxLo=0,minLa = 999,minLo = 999,randomLa,randomLo;
            for(int i = 0;i<pointArray.size();i++)
            {
                if(pointArray.getJSONObject(i).getDouble("latitude")>maxLa)
                    maxLa = pointArray.getJSONObject(i).getDouble("latitude");
                if(pointArray.getJSONObject(i).getDouble("longitude")>maxLo)
                    maxLo = pointArray.getJSONObject(i).getDouble("longitude");
                if(pointArray.getJSONObject(i).getDouble("latitude")<minLa)
                    minLa = pointArray.getJSONObject(i).getDouble("latitude");
                if(pointArray.getJSONObject(i).getDouble("longitude")<minLo)
                    minLo = pointArray.getJSONObject(i).getDouble("longitude");
            }
            randomLa = Double.parseDouble(decimalFormat1.format(minLa + ((maxLa-minLa)*random.nextDouble())));
            randomLo = Double.parseDouble(decimalFormat2.format(minLo + ((maxLo-minLo)*random.nextDouble())));
            jsonObject = new JSONObject();
            jsonObject.put("latitude",randomLa);
            jsonObject.put("longitude",randomLo);
            jsonObject.put("limitationsGoalsSexInfoId",limitationsGoalsSexInfoId);
            jsonObject.put("patternId",patternId);
            jsonObject.put("semesterId",semesterId);
            jsonObject.put("scoringType",1);
            runningRange = httpUtil.doPost(host+"/running/app/getRunningRange",jsonObject.toJSONString(),token);
            JSONArray signPoints = new JSONArray();
            if(runningRange == null){
                saveUserInfo(userInfo,userQQ,userId,userPassword);
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("获取跑步点位失败！");
                return;
            }
            jsonObject = JSONObject.parseObject(runningRange);
            if(jsonObject.getInteger("code")!=0){
                saveUserInfo(userInfo,userQQ,userId,userPassword);
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("获取跑步点位失败！");
                return;
            }
            targetPoints = new ArrayList<>();
            jsonObject = jsonObject.getJSONObject("data");
            pointArray = jsonObject.getJSONArray("signPoint");
            for(int i = 0;i<pointArray.size();i++){
                JSONObject signPointsObject = new JSONObject();
                signPointsObject.put("state",1);
                signPointsObject.put("signPoint",pointArray.getJSONObject(i).getString("id"));
                signPoints.add(signPointsObject);
                targetPoints.add(new Point(pointArray.getJSONObject(i).getDouble("latitude"),pointArray.getJSONObject(i).getDouble("longitude")));
            }
            JSONArray routineLine = new JSONArray();
            if(Main.baiduMapAk==null||Main.baiduMapAk.equals("")){
                saveUserInfo(userInfo,userQQ,userId,userPassword);
                Friend friend = bot.getFriend(userQQ);
                if(friend!=null)
                    friend.sendMessage("百度地图Ak配置有误，请联系管理员检查");
                return;
            }
            for(int i = 0;i<targetPoints.size();i++){
                double[] possint;
                //https://api.map.baidu.com/directionlite/v1/walking?origin=40.01116,116.339303&destination=39.936404,116.452562&ak=jIzl1tWSKs5fhvBy4d25ydE1x8wxtjiP
                String baiduMap;
                DecimalFormat decimalFormat3 = new DecimalFormat("0.000000");
                if(i == 0)
                    baiduMap = httpUtil.doGet(baiduMapHost+"/directionlite/v1/walking?coord_type=gcj02&origin="+decimalFormat3.format(randomLa)+","+decimalFormat3.format(randomLo)+"&destination="+targetPoints.get(0).latitude+","+targetPoints.get(0).longitude+"&ak="+Main.baiduMapAk,null);
                else baiduMap = httpUtil.doGet(baiduMapHost+"/directionlite/v1/walking?coord_type=gcj02&origin="+targetPoints.get(i-1).latitude+","+targetPoints.get(i-1).longitude+"&destination="+targetPoints.get(i).latitude+","+targetPoints.get(i).longitude+"&ak="+Main.baiduMapAk,null);
                JSONObject baiduRe = JSON.parseObject(baiduMap);
                baiduRe = baiduRe.getJSONObject("result").getJSONArray("routes").getJSONObject(0);
                JSONArray steps = baiduRe.getJSONArray("steps");
                for(int ii = 0;ii< steps.size();ii++){
                    String[] pathStr = steps.getJSONObject(ii).getString("path").split(";");
                    for(String paths:pathStr){
                        String[] pathPoint = paths.split(",");
                        JSONObject object = new JSONObject();
                        possint = bd2gcj(Double.parseDouble(pathPoint[1]),Double.parseDouble(pathPoint[0]));
                        object.put("longitude",possint[1] + Double.parseDouble(decimalFormat1.format((random.nextDouble()-0.5)/500000)));
                        object.put("latitude",possint[0] + Double.parseDouble(decimalFormat2.format((random.nextDouble()-0.5)/5000)));
                        routineLine.add(object);
                    }
                    //longitude小数点后3位开始随机
                }
            }
            String runningInfo = GenerateClass.getRunningDetail(semesterId,limitationsGoalsSexInfoId,validMileage,versionLabel,routineLine.toJSONString(),signPoints.toJSONString());
            String endJsonReturn = httpUtil.doPost(host + "/running/app/uploadRunningDetails",runningInfo,token);
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
    static double pi = 3.14159265358979324;
    static double a = 6378245.0;
    static double ee = 0.00669342162296594323;
    public final static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
    public static double[] gcj2bd(double lat, double lon) {
        double z = Math.sqrt(lon * lon + lat * lat) + 0.00002 * Math.sin(lat * x_pi);
        double theta = Math.atan2(lat, lon) + 0.000003 * Math.cos(lon * x_pi);
        double bd_lon = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return new double[]{bd_lat, bd_lon};
    }
    public static double[] bd2gcj(double lat, double lon) {
        double x = lon - 0.0065, y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        double gg_lon = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new double[]{gg_lat, gg_lon};
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
