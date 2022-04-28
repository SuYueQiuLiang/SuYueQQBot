package org.suyue.LezzBot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class GenerateClass {
    private static final double CALORIE_PER_MILEAGE = 58.3;
    public static String getRunningDetail(String semesterId,String limitationsGoalsSexInfoId,double validMileage,String appVersion,String runningInfo,String signPoint){
        Random random = new Random(System.currentTimeMillis());
        //随机偏移一定的跑步路程避免整数
        double partValid = random.nextDouble();
        double totMileage = validMileage - (partValid * random.nextDouble());
        validMileage -= partValid;
        DecimalFormat df = new DecimalFormat("#.00");
        validMileage = Double.parseDouble(df.format(validMileage));
        JSONObject content = new JSONObject();
        content.put("scoringType", 1);
        content.put("semesterId", semesterId);
        content.put("effectiveMileage", validMileage);
        content.put("totalMileage", totMileage);
        content.put("deviceType", "MI8");
        content.put("gpsMileage",totMileage);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int randomTime = random.nextInt(120);
        long time = System.currentTimeMillis()-(randomTime*60*1000),time2 = time-((random.nextInt(1000)/100+15)*60*1000);
        Date date1 = new Date(time2);
        Date date2 = new Date(time);
        content.put("startTime",simpleDateFormat.format(date1));
        content.put("endTime",simpleDateFormat.format(date2));
        int calorie = (int) (totMileage * CALORIE_PER_MILEAGE);
        int keeptime = (int) (date2.getTime() - date1.getTime()) / 1000;
        content.put("keepTime", keeptime);
        content.put("calorie", calorie);
        content.put("effectivePart", 1);
        content.put("limitationsGoalsSexInfoId", limitationsGoalsSexInfoId);
        content.put("systemVersion", "11.0.1");
        content.put("signPoint", new JSONArray());
        content.put("routineLine", JSONArray.parseArray(runningInfo));
        int avePace = ((int)((date2.getTime()-date1.getTime())/1000/totMileage))*1000;
        content.put("avePace",avePace);
        content.put("totalPart", 1);
        double pace = 0.5 + random.nextInt(6) / 10.0;
        int paceNumber = (int)(totMileage*1000/pace/2);
        content.put("paceNumber",paceNumber);
        content.put("paceRange", pace);
        content.put("type", "定点跑");
        content.put("signPoint",JSONArray.parseArray(signPoint));
        content.put("uneffectiveReason","");
        content.put("appVersion",appVersion);
        content.put("signDigital",Encrypter.getSha1(validMileage
                + "1"
                + simpleDateFormat.format(date1)
                + calorie
                + avePace
                + keeptime
                + paceNumber
                + totMileage
                + "1" + Encrypter.run_salt));
        System.out.println(content.toString());
        return content.toString();
    }

//    {
//        "type": "定点跑",
//            "uneffectiveReason": "",
//            "appVersion": "3.2.0",
//            "routineLine": [],
//        "startTime": "2022-04-15 16:05:39",
//            "effectiveMileage": 1.0986062508951875,
//            "semesterId": "8a9780647ef79db8017f006a4e700047",
//            "paceNumber": 0,
//            "effectivePart": 1,
//            "calorie": 70,
//            "systemVersion": "15.4.1",
//            "gpsMileage": 1.0986062508951875,
//            "deviceType": "iPhone 12 Pro Max",
//            "avePace": 214000,
//            "keepTime": 236,
//            "totalPart": 1,
//            "totalMileage": 1.0986062508951875,
//            "scoringType": 1,
//            "paceRange": 0.59999999999999998,
//            "endTime": "2022-04-15 16:09:44",
//            "signDigital": "de5a47d835c1f2523701ce2e8685a9f8f0e0b172",
//            "limitationsGoalsSexInfoId": "402888da7c3a16bb017c3a172c30018c",
//            "signPoint": [{
//        "state": 1,
//                "signPoint": "8a9780e17c6d58bb017c6d5a4fdb04bc"
//    }, {
//        "state": 1,
//                "signPoint": "8a9780e17c6d58bb017c6d5a4fdb04bb"
//    }, {
//        "state": 1,
//                "signPoint": "8a9780e17c6d58bb017c6d5a4fd4046b"
//    }]
//    }

}
