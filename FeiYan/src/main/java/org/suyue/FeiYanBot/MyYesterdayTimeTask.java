package org.suyue.FeiYanBot;

import java.util.TimerTask;

public class MyYesterdayTimeTask extends TimerTask {
    @Override
    public void run() {
        Data.yesterdayData = Data.bufferData;
        Data.bufferData = null;
    }
}
