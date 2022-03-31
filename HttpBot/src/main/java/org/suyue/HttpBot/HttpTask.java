package org.suyue.HttpBot;

import net.mamoe.mirai.Bot;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Timer;

public class HttpTask {
    //时间间隔
    private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;
    public MyTimeTask task;
    public Timer timer;
    public long userQQ;
    public int hourOfDay;
    private final Bot bot;
    public String httpUrl,param,taskName;
    public Map<String, String> headers;
    public HttpTask(int hourOfDay, Bot bot, String taskName, long userQQ, String httpUrl, Map<String, String> headers, String param) {
        this.hourOfDay = hourOfDay;
        this.bot = bot;
        this.taskName = taskName;
        this.httpUrl = httpUrl;
        this.headers = headers;
        this.param = param;
        this.userQQ = userQQ;
        if (hourOfDay != -1) {
            Calendar calendar = Calendar.getInstance();calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date date = calendar.getTime(); //第一次执行定时任务的时间

            //如果第一次执行定时任务的时间 小于 当前的时间
            //此时要在 第一次执行定时任务的时间 加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。
            if (date.before(new Date())) {
                date = this.addDay(date, 1);
            }
            timer = new Timer();
            task = new MyTimeTask(bot,taskName, userQQ, httpUrl, headers,param);
            //安排指定的任务在指定的时间开始进行重复的固定延迟执行。
            timer.schedule(task, date, PERIOD_DAY);
        }
    }
    public void flushTask(){
        if (hourOfDay != -1) {
            Calendar calendar = Calendar.getInstance();calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date date = calendar.getTime(); //第一次执行定时任务的时间
            //如果第一次执行定时任务的时间 小于 当前的时间
            //此时要在 第一次执行定时任务的时间 加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。
            if (date.before(new Date())) {
                date = this.addDay(date, 1);
            }
            if(timer!=null){
                timer.cancel();
                task.cancel();
            }
            timer = new Timer();
            task = new MyTimeTask(bot,taskName, userQQ, httpUrl, headers,param);
            timer.schedule(task, date, PERIOD_DAY);
        }else timer.cancel();
    }
    // 增加或减少天数
    public Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, num);
        return startDT.getTime();
    }
}
