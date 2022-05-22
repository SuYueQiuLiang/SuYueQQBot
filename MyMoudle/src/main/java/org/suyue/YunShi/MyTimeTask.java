package org.suyue.YunShi;

import net.mamoe.mirai.Bot;

import java.util.TimerTask;

public class MyTimeTask<T> extends TimerTask {
    private final Bot bot;
    private final T t;
    private static final HttpUtil httpUtil = new HttpUtil();
    public MyTimeTask(Bot bot,T t){
        this.bot = bot;
        this.t =t;
    }
    @Override
    public void run() {

    }
}
