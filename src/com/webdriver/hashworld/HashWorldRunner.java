package com.webdriver.hashworld;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HashWorldRunner {

    public static void main(String[] args) throws Exception {

        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        //启动时间(秒)
        Date startDate = df.parse("2018/03/30 00:05:00");
        long startTime = (startDate.getTime() - System.currentTimeMillis()) / 1000;

        //间隔时间(秒)
        long intervalTime = 24 * 60 * 60 + 60;
        Runnable runnable = new Runnable() {
            public void run() {
                // task to run goes here
                System.out.println("-------------------执行时间 " + df.format(new Date()) + " -------------------");

                HashWorldChecker check = new HashWorldChecker();
                try {

                    check.checkEveryNum(check, check.fileIn);
                    check.outputBalance();

                } catch (Exception e) {
                    System.out.println(e.getLocalizedMessage());
                }
            }
        };
        ScheduledExecutorService service = Executors
                .newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(runnable, startTime, intervalTime, TimeUnit.SECONDS);
    }
}
