package org.suyue.HttpBot;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;

import java.util.Map;
import java.util.Objects;
import java.util.TimerTask;

public class MyTimeTask extends TimerTask {
    public MyTimeTask(Bot bot, String taskName, long userQQ, String httpUrl, Map<String, String> headers, String param) {
        this.bot = bot;
        this.httpUrl = httpUrl;
        this.param = param;
        this.userQQ = userQQ;
        this.headers = headers;
        this.taskName = taskName;
    }

    private final Bot bot;
    private final String httpUrl,param,taskName;
    private final long userQQ;
    private static final HttpUtil httpUtil = new HttpUtil();
    private final Map<String,String> headers;
    @Override
    public void run() {
        Friend friend = bot.getFriend(userQQ);
        if(Objects.equals(param, "")){
            if(friend == null)
                httpUtil.doGet(httpUrl,headers);
            else friend.sendMessage("自动Http任务" + taskName + "请求结束，结果如下：\n"+httpUtil.doGet(httpUrl,headers));
        }else {
            if(friend == null)
                httpUtil.doPost(httpUrl,headers,param);
            else friend.sendMessage("自动Http任务" + taskName + "请求结束，结果如下：\n"+httpUtil.doPost(httpUrl,headers,param));
        }
    }
}
