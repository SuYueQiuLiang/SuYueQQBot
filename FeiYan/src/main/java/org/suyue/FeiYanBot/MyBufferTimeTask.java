package org.suyue.FeiYanBot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

import java.util.HashMap;
import java.util.TimerTask;

public class MyBufferTimeTask extends TimerTask {
    @Override
    public void run() {
        HttpUtil httpUtil = new HttpUtil();
        try {
            Data.bufferData = JSON.parseObject(httpUtil.doGet("https://interface.sina.cn/news/wap/fymap2020_data.d.json",new HashMap<>()));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
