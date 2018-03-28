package com.webdriver.bihu.openpage;

import com.webdriver.bihu.TimeCheck;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Created by Jackie.Liu on 24/03/2018.
 */


public class BihuCheckerOpenPage extends Thread {
    private static String style = "HH:mm:ss";
    private static String startTime1 = "05:30:00";
    private static String endTime1 = "09:30:00";
    private WebDriver driver;
    private String baseUrl;
    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();
    private String username = "";
    private String password = "";

    public BihuCheckerOpenPage(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void run() {

        try {
            init();
            doLogin();
            runCheck();
            closeOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() throws Exception {

        driver = new FirefoxDriver();
//        driver = new ChromeDriver();
        baseUrl = "https://www.bihu.com/login";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.manage().window().setPosition(new Point(0, 0)); //指定窗口坐标
        driver.manage().window().setSize(new Dimension(800, 700)); //指定窗口大小
        sleep(1000);
    }

    public void doLogin() throws Exception {


        driver.get(baseUrl + "/");
        driver.findElement(By.linkText("登录")).click();
        sleep(1000);
        driver.findElement(By.id("loginName")).clear();
        driver.findElement(By.id("loginName")).sendKeys(username);

        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys(password);

        driver.findElement(By.cssSelector("button.LoaderButton.from-item-btn")).click();
        sleep(1000);
        waitForPageLoad(driver);

        for (int second = 0; ; second++) {
            if (second >= 60) Assert.fail("timeout");
            try {
                if (isElementPresent(By.cssSelector("img[alt=\"Logo\"]"))) break;
            } catch (Exception e) {
            }
            sleep(1000);
        }
    }

    public void runCheck() throws Exception {

        // 检查分钟间隔数
        final long timeInterval = 2 * 60 * 1000;
        final long speedInterval = 1 * 60 * 1000;

        while (true) {
            checkFiveZan();

            try {
                Date startTime = new SimpleDateFormat(style).parse(startTime1);
                Date endTime = new SimpleDateFormat(style).parse(endTime1);
                SimpleDateFormat df1 = new SimpleDateFormat(style);
                Date nowTime = new SimpleDateFormat(style).parse(df1.format(new Date()));

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

    private void checkFiveZan() throws Exception {

        driver.findElement(By.cssSelector("img[alt=\"Logo\"]")).click();
        sleep(2000);
        driver.findElement(By.linkText("关注")).click();
//        driver.findElement(By.linkText("推荐")).click();
        sleep(3000);
        waitForPageLoad(driver);

        //时间格式
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        System.out.println(df.format(day));


        //当前时间
        WebElement ele = driver.findElement(By.xpath("//div[@id='root']/div/div/div/div[2]/div/ul[2]/div/div/div[2]/div/p[2]"));
        System.out.println("====================" + username + "====================");
        System.out.println("最新文章发表于" + ele.getText() + ", 当前时间 " + df.format(day));
//        System.out.println(ele.getCssValue("color"));

        //判断前五篇文章点赞情况
        String[] papers = new String[5];
        papers[0] = "//div[@id='root']/div/div/div/div[2]/div/ul[2]/div/div[2]/div/div[2]/div[2]/button[1]";
        papers[1] = "//div[@id='root']/div/div/div/div[2]/div/ul[2]/div[2]/div[2]/div/div[2]/div[2]/button[1]";
        papers[2] = "//div[@id='root']/div/div/div/div[2]/div/ul[2]/div[3]/div[2]/div/div[2]/div[2]/button[1]";
        papers[3] = "//div[@id='root']/div/div/div/div[2]/div/ul[2]/div[4]/div[2]/div/div[2]/div[2]/button[1]";
        papers[4] = "//div[@id='root']/div/div/div/div[2]/div/ul[2]/div[5]/div[2]/div/div[2]/div[2]/button[1]";

        //前五篇文章缩略图
        String[] pics = new String[5];
        pics[0] = "//img[@alt='文章缩略图']";
        pics[1] = "(//img[@alt='文章缩略图'])[2]";
        pics[2] = "(//img[@alt='文章缩略图'])[3]";
        pics[3] = "(//img[@alt='文章缩略图'])[4]";
        pics[4] = "(//img[@alt='文章缩略图'])[5]";


        //点赞分析
        for (int i = 0; i < 5; i++) {
            String paper = papers[i];
            WebElement zan = driver.findElement(By.xpath(paper));

            int count = i + 1;
            String zanNum = "第" + count + "篇文章点赞数: " + zan.getText();
            int x = Integer.parseInt(zan.getText());


            //点过赞的颜色
            java.awt.Color color1 = new java.awt.Color(0, 123, 255);
            //取赞颜色
            java.awt.Color color2 = getZanColor(zan);

            //判断是否点过赞
            if (color1.equals(color2)) {
                System.out.println(zanNum + ", 已点过赞。" + color2.toString());
            } else {
                if (x < 500) {
                    String pic = pics[i];
                    CommentAndUp(pic);
                } else {
                    System.out.println(zanNum + ", 没点过赞，大于500，跳过。" + color2.toString());
                }
                sleep(2000);
            }
        }
    }

    public void closeOut() throws Exception {
        //点击退出登录
        driver.findElement(By.cssSelector("img[alt=\"Logo\"]")).click();
        sleep(1000);
        mouseMove(driver);

        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            Assert.fail(verificationErrorString);
        }
    }

    private void mouseMove(WebDriver dr) throws Exception {
        //将鼠标移动，并选择
        Actions act = new Actions(dr);
        WebElement dropDown = dr.findElement(By.cssSelector("img[alt='个人中心']"));
        WebElement logout = dr.findElement(By.linkText("退出"));

        act.click(dropDown).perform();
        sleep(1000);
        act.moveToElement(logout).click().perform();
    }

    private void CommentAndUp(String pic) {
        //原窗口句柄
        String currentWindow = driver.getWindowHandle();

        //点击打开新窗口
        driver.findElement(By.xpath(pic)).click();
//        String handle_new = driver.getWindowHandle();

        Set<String> handles = driver.getWindowHandles();
        Iterator<String> it = handles.iterator();

        //hasNext检查序列中是否还有元素
        while (it.hasNext()) {
            String handle = it.next();
            if (currentWindow.equals(handle)) continue;
            WebDriver window = driver.switchTo().window(handle);

            //点赞按钮
            WebElement iZan = driver.findElement(By.xpath("//div[@id='root']/div/div/div/div/div[2]/div/div/div[5]/div/div[2]/button"));

            //点过赞的颜色
            java.awt.Color color1 = new java.awt.Color(0, 123, 255);
            //取赞颜色
            java.awt.Color color2 = getZanColor(iZan);
            String zanNum = iZan.getText();
            System.out.println("********** 没点过赞。内部赞，赞前数值： " + zanNum + ", 赞前颜色："+ color2.toString());

            try {
                iZan.click();
                sleep(200);
                iZan.click();
                sleep(200);
                iZan.click();
                sleep(200);
                iZan.click();
                sleep(200);
                iZan.click();
                sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            color2 = getZanColor(iZan); //赞后颜色
            String afterZan = iZan.getText();
            System.out.println("********** 没点过赞。内部赞，赞后数值： " + afterZan + ", 赞后颜色："+ color2.toString());

            while (zanNum.equals(afterZan)) {
                System.out.println("------------- 没点过赞。第一次点赞不成功，循环点赞，赞前数值： " + afterZan + ", 赞前颜色："+ color2.toString());
                try {
                    driver.navigate().refresh();
                    sleep(2000);
                    waitForPageLoad(driver);
                    waitForElement(driver, By.xpath("//div[@id='root']/div/div/div/div/div[2]/div/div/div[5]/div/div[2]/button"));

                    iZan = driver.findElement(By.xpath("//div[@id='root']/div/div/div/div/div[2]/div/div/div[5]/div/div[2]/button"));
                    iZan.click();
                    sleep(200);
                    iZan.click();
                    sleep(200);
                    color2 = getZanColor(iZan); //赞后颜色
                    afterZan = iZan.getText();

                    sleep(2000);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("------------- 没点过赞。第一次点赞不成功，循环点赞，赞后数值： " + afterZan + ", 赞后颜色："+ color2.toString());
            }


            //评论内容
            driver.findElement(By.id("content")).click();
            driver.findElement(By.id("content")).clear();
            if (this.username.substring(0,3).equals("136")) {
                driver.findElement(By.id("content")).sendKeys("虚心使人进步，点赞积累财富！ " + afterZan);
            } else {
                driver.findElement(By.id("content")).sendKeys("每天学习一点点，每天进步一点点！ " + afterZan);
            }


            //发表按钮
            driver.findElement(By.xpath("//div[@id='root']/div/div/div/div/div[2]/div/div/div[7]/button")).click();
            driver.close();
        }

        driver.switchTo().window(currentWindow);
    }

    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    private String closeAlertAndGetItsText() {
        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            if (acceptNextAlert) {
                alert.accept();
            } else {
                alert.dismiss();
            }
            return alertText;
        } finally {
            acceptNextAlert = true;
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

    private static void waitForPageLoad(WebDriver driver) {
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

    private static Color getZanColor(WebElement zan) {
        String color = zan.getCssValue("color");
        color = color.substring(4, color.length() - 1);
        String[] strs = color.split(",");
//        for (int i = 0, len = strs.length; i < len; i++) {
//            System.out.println(strs[i].toString());
//        }

        java.awt.Color color2 = new java.awt.Color(Integer.parseInt(strs[0].trim()), Integer.parseInt(strs[1].trim()), Integer.parseInt(strs[2].trim()));
        return color2;
    }


}