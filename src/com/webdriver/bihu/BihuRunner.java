package com.webdriver.bihu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Thread.sleep;


/**
 * Created by Jackie.Liu on 23/03/2018.
 */

public class BihuRunner {
    //    private static String fileIn = "C:/DevTools/Blockchain/src/com/webdriver/bihu/CheckNum.txt";
    private static String fileIn = "/Users/Jackie.Liu/DevTools/Blockchain/src/com/webdriver/bihu/CheckNum.txt";
    private static String[][] users = new String[2][2];
    private static String style = "HH:mm:ss";

    private static String startTime1 = "05:30:00";
    private static String endTime1 = "09:30:00";

    public static void main(String[] args) {

        BihuRunner running = new BihuRunner();
        running.loadUser();

        // 每3分钟检查一次
        final long timeInterval = 3 * 60 * 1000;
        final long speedInterval = 1 * 60 * 1000;

        Runnable runnable = new Runnable() {
            public void run() {
                while (true) {
                    // ------- code for task to run
                    BihuChecker checker1 = new BihuChecker(users[0][0], users[0][1]);
                    checker1.start();
                    try {
                        sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    BihuChecker checker2 = new BihuChecker(users[1][0], users[1][1]);
//                    checker2.start();

                    try {
                        Date startTime = new SimpleDateFormat(style).parse(startTime1);
                        Date endTime = new SimpleDateFormat(style).parse(endTime1);
                        SimpleDateFormat df = new SimpleDateFormat(style);
                        Date nowTime = new SimpleDateFormat(style).parse(df.format(new Date()));

                        if (TimeCheck.isEffectiveDate(nowTime, startTime, endTime)) {
                            sleep(speedInterval);
                        } else {
                            sleep(timeInterval);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }


    private static void loadUser() {
        try {
            FileReader fr = new FileReader(fileIn);
            BufferedReader br = new BufferedReader(fr);

            String loginNum;
            int i = 0, j = 0;
            while ((loginNum = br.readLine()) != null) {

//                System.out.println(loginNum);
                String[] strs = loginNum.split(",");
                users[i][j] = strs[0].trim();
                users[i][j + 1] = strs[1].trim();
                i++;
            }

            br.close();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


