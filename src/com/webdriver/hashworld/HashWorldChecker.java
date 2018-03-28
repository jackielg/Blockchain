package com.webdriver.hashworld;

import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.lang.Thread.sleep;
import static org.junit.Assert.fail;

/**
 * Created by Jackie.Liu on 16/03/2018.
 */
public class HashWorldChecker {

    //    public static String fileIn = "C:/DevTools/Blockchain/src/com/webdriver/hashworld/CheckNum.txt";
    public static String fileIn = "/Users/Jackie.Liu/DevTools/Blockchain/src/com/webdriver/hashworld/CheckNum.txt";
    static Date day = new Date();
    static SimpleDateFormat df_forFile = new SimpleDateFormat("yyyy-MM-dd");
    //    public static String fileOut = "C:/DevTools/Blockchain/src/com/webdriver/hashworld/CheckedNum " + df_forFile.format(day) + ".txt";
    public static String fileOut = "/Users/Jackie.Liu/DevTools/Blockchain/src/com/webdriver/hashworld/CheckedNum " + df_forFile.format(day) + ".txt";
    private static Map<String, String> Balance = new HashMap<String, String>();
    private static Integer count = 0;
    private WebDriver driver;
    private String baseUrl;
    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();

    public static void main(String[] args) {

        HashWorldChecker check = new HashWorldChecker();

        try {

            checkEveryNum(check, fileIn);
            outputBalance();

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());

        }

    }

    public static void checkEveryNum(HashWorldChecker check, String file) {
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            String loginNum;

            while ((loginNum = br.readLine()) != null) {

//                System.out.println(loginNum);
                count++;
                check.init();
                check.doLogin(loginNum.trim());
                try {
                    check.runCheck(loginNum);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                check.closeOut();
            }

            br.close();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void outputBalance() throws IOException {

//        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOrFilename, true)));

        //文件写入流
        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fileOut, true));
        BufferedWriter bw = new BufferedWriter(out);
        double count = 0;

        //计算总额
        for (String key : Balance.keySet()) {
//            bw.append(key);
//            bw.append(" , ");
//            bw.append(Balance.get(key));

            double d = parseCNY(Balance.get(key));
            count = count + d;
            System.out.println("key = " + key + " and value = " + Balance.get(key));
        }

        bw.newLine();
        bw.append("Balance.size() is：" + Balance.size() + ", Value is: " + count);
        System.out.println("Balance.size() is：" + Balance.size() + ", Value is: " + count);

        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        bw.newLine();
        bw.append("-------------------结束时间 " + df.format(new Date()) + " -------------------");
        System.out.println("-------------------结束时间 " + df.format(new Date()) + " -------------------");

        bw.flush();
        bw.close();
        out.close();

    }

    public static void outputNow(String num, String CNY) throws IOException {

        //文件写入流
        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fileOut, true));
        BufferedWriter bw = new BufferedWriter(out);

        bw.append(num);
        bw.append(" , ");
        bw.append(CNY);
        bw.newLine();
        bw.flush();

        bw.close();
        out.close();
    }

    static double parseCNY(String CNY) {
        String money = CNY.substring(0, CNY.length() - 3);
        return Double.parseDouble(money.trim());
    }

    public static void waitForPageLoad(WebDriver driver) {
        Function<WebDriver, Boolean> waitFn = new Function<WebDriver, Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState")
                        .equals("complete");
            }
        };
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(waitFn);
    }

    public void init() throws Exception {
//        driver = new FirefoxDriver();
        driver = new ChromeDriver();
        baseUrl = "https://game.hashworld.top/#!/register";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.manage().window().setPosition(new Point(0, 0)); //指定窗口坐标
        driver.manage().window().setSize(new Dimension(400, 800)); //指定窗口大小
        driver.get(baseUrl + "/#!/register");
//        sleep(2000);
    }

    public void doLogin(String loginNum) throws Exception {

        sleep(1000);
        driver.findElement(By.xpath("//div[4]/p")).click();
        sleep(1000);
        driver.findElement(By.id("logintel")).clear();
        driver.findElement(By.id("logintel")).sendKeys(loginNum);
        sleep(1000);
        driver.findElement(By.id("loginpassword")).clear();
        driver.findElement(By.id("loginpassword")).sendKeys("Liuxb0504");
        sleep(1000);
        driver.findElement(By.id("loginsubmit")).click();
        sleep(1000);

        waitForPageLoad(driver);
        waitForElement(driver, By.cssSelector("span.help"));
    }

    public void runCheck(String loginNum) throws Exception {

        //强制等待5秒，加载剩余机会数
//        sleep(5000);
//        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector("span"))));
//        driver.navigate().refresh();

//        sleep(2000);
        waitForPageLoad(driver);

        String chance = driver.findElement(By.cssSelector("span")).getText();
        if (!chance.equals("0")) {
            //机会数不等于0，再确认一次
            sleep(2000);
            chance = driver.findElement(By.cssSelector("span")).getText();
        }

        int b = Integer.parseInt(chance);
//        System.out.println("chance = " + chance);


        //点击前3个图片
        String[] papers = new String[3];
        papers[0] = "//hw-treasure-block/div/img";
        papers[1] = "//hw-treasure-block[2]/div/img";
        papers[2] = "//hw-treasure-block[3]/div/img";

        for (int i = 0; i < b; i++) {
            //图片操作
            sleep(1000);
            waitForPageLoad(driver);
            waitForElement(driver, By.xpath(papers[i]));
            driver.findElement(By.xpath(papers[i])).click();  //点击地标


            sleep(1000);
            waitForPageLoad(driver);
            waitForElement(driver, By.xpath("//div[3]/div[2]/div"));
            driver.findElement(By.xpath("//div[3]/div[2]/div")).click();  //开


            sleep(1000);
            waitForPageLoad(driver);
            waitForElement(driver, By.xpath("//button"));
            driver.findElement(By.xpath("//button")).click();  //返回

            waitForElement(driver, By.cssSelector("span.help"));
        }


        //点击标签“我”
        sleep(1000);
        waitForPageLoad(driver);
        waitForElement(driver, By.xpath("//div[3]/img"));
        driver.findElement(By.xpath("//div[3]/img")).click();


        //点击“我的钱包”，强制等2秒
        sleep(2000);
        waitForPageLoad(driver);
        waitForElement(driver, By.xpath("//hw-my/div[2]/div"));
        driver.findElement(By.xpath("//hw-my/div[2]/div")).click();
//        driver.findElement(By.cssSelector("div.perState.ng-scope")).click();

        //获取“估算钱包总资产”
//        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector("h1.wa-hl-money.ng-binding"))));

        waitForPageLoad(driver);
        String CNY = driver.findElement(By.cssSelector("h1.wa-hl-money.ng-binding")).getText();

        while (CNY.contains("0.00")) {
            sleep(2000);
            waitForPageLoad(driver);

            driver.navigate().refresh();
            sleep(2000);
            CNY = driver.findElement(By.cssSelector("h1.wa-hl-money.ng-binding")).getText();
        }


//        if (CNY.contains("0.00")) {
//            //没取到金额，再强制等2秒
//            sleep(2000);
//            waitForPageLoad(driver);
//            driver.navigate().refresh();
//            sleep(2000);
//            CNY = driver.findElement(By.cssSelector("h1.wa-hl-money.ng-binding")).getText();
//        }
        outputNow(loginNum, CNY);
        System.out.println("### " + count + ": " + loginNum + " has " + CNY + ". ###");
        Balance.put(loginNum, CNY);

        //浏览器回退
//        sleep(2000);
        waitForPageLoad(driver);
//        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        driver.navigate().back();
        waitForPageLoad(driver);


        //点击退出登录
//        sleep(2000);
        waitForPageLoad(driver);
//        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        driver.findElement(By.cssSelector("button")).click();
    }

    public void closeOut() throws Exception {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }

    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private void waitForElement(WebDriver driver, By by) throws Exception {
        for (int second = 0; ; second++) {
            if (second >= 60) Assert.fail("timeout");
            try {
                if (isElementPresent(by)) {
                    break;
                } else {
                    driver.navigate().refresh();
                }
            } catch (Exception e) {
            }
            sleep(1000);
        }
    }

}

