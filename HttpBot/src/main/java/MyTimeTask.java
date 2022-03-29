import net.mamoe.mirai.Bot;

import java.util.TimerTask;

public class MyTimeTask extends TimerTask {
    private final Bot bot;
    private final String userId,userPassword;
    private final long userQQ;
    private static final HttpUtil httpUtil = new HttpUtil();
    private final double validMileage;
    public MyTimeTask(Bot bot, long userQQ, String userId, String userPassword, double validMileage){
        this.validMileage = validMileage;
        this.bot = bot;
        this.userQQ = userQQ;
        this.userId = userId;
        this.userPassword = userPassword;
    }
    @Override
    public void run() {
        HttpUtil
    }
}
