package org.suyue.FeiYanBot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimerTask;

public class MyTimeTask extends TimerTask {

    @Override
    public void run() {
        HttpUtil httpUtil = new HttpUtil();
        try {
            Data.data = JSON.parseObject(httpUtil.doGet("https://interface.sina.cn/news/wap/fymap2020_data.d.json",new HashMap<>()));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
