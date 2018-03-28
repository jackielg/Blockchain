package com.webdriver.bihu.longsession;

import java.io.BufferedReader;
import java.io.FileReader;

import static java.lang.Thread.sleep;


/**
 * Created by Jackie.Liu on 23/03/2018.
 */

public class BihuRunnerLongSession {
    
    private static String fileIn = "src/com/webdriver/bihu/CheckNum.txt";
    private static String[][] users = new String[2][2];


    public static void main(String[] args) {

        BihuRunnerLongSession running = new BihuRunnerLongSession();
        running.loadUser();


        Runnable runnable = new Runnable() {
            public void run() {

                // ------- code for task to run
                BihuCheckerLongSession checker1 = new BihuCheckerLongSession(users[0][0], users[0][1]);
                checker1.start();
                try {
                    sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

//                BihuCheckerLongSession checker2 = new BihuCheckerLongSession(users[1][0], users[1][1]);
//                checker2.start();


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


