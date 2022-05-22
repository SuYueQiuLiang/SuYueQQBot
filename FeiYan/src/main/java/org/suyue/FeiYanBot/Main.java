package org.suyue.FeiYanBot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import org.suyue.bot.SuYueBotMod;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

public class Main implements SuYueBotMod {
    private static final long PERIOD_HALF_HOUR = 30 * 60 * 1000;
    private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;
    public MyTimeTask task;
    public MyBufferTimeTask bufferTimeTask;
    public MyYesterdayTimeTask yesterdayTimeTask;
    public Timer timer, bufferTimer, changeTimer;

    public Main() {
        timer = new Timer();
        bufferTimer = new Timer();
        changeTimer = new Timer();
        task = new MyTimeTask();
        bufferTimeTask = new MyBufferTimeTask();
        yesterdayTimeTask = new MyYesterdayTimeTask();
        timer.schedule(task, 0, PERIOD_HALF_HOUR);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date date1 = calendar.getTime();
        if (date1.before(new Date())) {
            date1 = this.addDay(date1, 1);
        }
        changeTimer.schedule(yesterdayTimeTask, date1, PERIOD_DAY);
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date date2 = calendar.getTime();
        if (date2.before(new Date())) {
            date2 = this.addDay(date2, 1);
        }
        bufferTimer.schedule(bufferTimeTask, date2, PERIOD_DAY);
    }

    // 增加或减少天数
    public Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, num);
        return startDT.getTime();
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

    @Override
    public void unloadMod() {
        timer.cancel();
    }

    private String getStringWithCity(String cityName) {
        StringBuilder builder = new StringBuilder();
        builder.append(getLastDataTime()).append("\n");
        int now = getNumberWithFullName(Data.data, cityName);
        if(now == -1)
            return "未查询到 " + cityName + "的疫情详情";
        builder.append("现有确诊：").append(now).append("\n");
        if (Data.yesterdayData == null)
            builder.append("暂无昨日数据！");
        else
            builder.append("相较昨日，确诊人数：").append(now-getNumberWithFullName(Data.yesterdayData,cityName));
        return builder.toString();
    }

    private String getLastDataTime() {
        return Data.data.getJSONObject("data").getString("times");
    }

    private int getNumberWithFullName(JSONObject data, String name) {
        JSONArray array = data.getJSONObject("data").getJSONArray("list");
        for (int i = 0; i < array.size(); i++) {
            if (array.getJSONObject(i).getString("name").replace("市", "").replace("省", "").equals(name)) {
                return Integer.parseInt(array.getJSONObject(i).getString("econNum"));
            } else {
                JSONArray city = array.getJSONObject(i).getJSONArray("city");
                for (int ii = 0; ii < city.size(); ii++) {
                    if (city.getJSONObject(ii).getString("name").replace("市", "").replace("省", "").equals(name) || city.getJSONObject(ii).getString("mapName").equals(name)) {
                        return Integer.parseInt(city.getJSONObject(ii).getString("econNum"));
                    }
                }
            }
        }
        return -1;
    }
}
