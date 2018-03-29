package com.webdriver.bihu.openpage;

import com.webdriver.bihu.TimeCheck;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.chrome.ChromeDriver;
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

//        driver = new FirefoxDriver();
        driver = new ChromeDriver();
        baseUrl = "https://www.bihu.com/login";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.manage().window().setPosition(new Point(0, 0)); //指定窗口坐标
        driver.manage().window().setSize(new Dimension(800, 700)); //指定窗口大小
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
        waitForElement(driver, By.cssSelector("img[alt=\"Logo\"]"));
        sleep(2000);
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

        driver.findElement(By.linkText("关注")).click();
//        driver.findElement(By.linkText("推荐")).click();

        //等第一篇文章的发布时间
        sleep(6000);
        waitForPageLoad(driver);
        waitForElement(driver, By.xpath("//div[@id='root']/div/div/div/div[2]/div/ul[2]/div/div/div[2]/div/p[2]"));

        //时间格式
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        //当前时间
        WebElement ele = driver.findElement(By.xpath("//div[@id='root']/div/div/div/div[2]/div/ul[2]/div/div/div[2]/div/p[2]"));
        System.out.println("====================" + username + "====================");
        System.out.println("最新文章发表于" + ele.getText() + ", 当前时间 " + df.format(day));


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

            //"关注"页面的[赞]元素
            WebElement zan = driver.findElement(By.xpath(paper));

            //点过赞的颜色
            java.awt.Color color1 = new java.awt.Color(0, 123, 255);
            //取赞颜色
            java.awt.Color color2 = getColor(zan, "color");

            //判断是否点过赞
            int count = i+1;
            String zanValue = zan.getText();
            if (color1.equals(color2)) {
                System.out.println("### 第"+count+"篇文章，已点过赞。赞数值:" + zanValue + ", 赞颜色:" + color2.toString());
            } else {
                int x = Integer.parseInt(zanValue);
                if (x > 400) {
                    System.out.println("### 第"+count+"篇文章，没点过赞。大于500跳过。赞数值:" + zanValue + ", 赞颜色:" + color2.toString());
                } else {
                    String pic = pics[i];
                    CommentAndUp(pic);

                    //返回父窗口
                    driver.navigate().refresh();
                    sleep(3000);
                    waitForPageLoad(driver);
                    waitForElement(driver, By.xpath(paper));
                    //返回父窗口再次判断点赞是否成功
                    zan = driver.findElement(By.xpath(paper));
                    color2 = getColor(zan, "color");
                    zanValue = zan.getText();

                    int num = 0;
                    while(!color1.equals(color2)) {
                        num++;
                        if (num >  3) Assert.fail("timeout");
                        System.out.println("$$$ 第"+count+"篇文章，子窗口内点赞不成功，再次弹出子窗口点赞。赞数值:" + zanValue + ", 赞颜色:" + color2.toString());
                        CommentAndUp(pic);

                        //返回父窗口
                        driver.navigate().refresh();
                        sleep(3000);
                        waitForPageLoad(driver);
                        waitForElement(driver, By.xpath(paper));
                        //返回父窗口再次判断点赞是否成功
                        zan = driver.findElement(By.xpath(paper));
                        color2 = getColor(zan, "color");
                        zanValue = zan.getText();
                    }
                }
            }
        }
    }

    private void CommentAndUp(String pic) {
        //原窗口句柄
        String fatherWindow = driver.getWindowHandle();

        //点击打开新窗口
        driver.findElement(By.xpath(pic)).click();

        Set<String> handles = driver.getWindowHandles();
        Iterator<String> it = handles.iterator();

        //hasNext检查序列中是否还有元素
        while (it.hasNext()) {
            String handle = it.next();
            if (fatherWindow.equals(handle)) continue;

            //找到新窗口
            WebDriver window = driver.switchTo().window(handle);

            //子页面内-点赞按钮
            String zanPath = "//div[@id='root']/div/div/div/div/div[2]/div/div/div[5]/div/div[2]/button";
            WebElement iZan = driver.findElement(By.xpath(zanPath));
            //===================
            //printWebElement(iZan);
            //===================

            //弹出窗口，没点过赞的背景颜色
            java.awt.Color color1 = new java.awt.Color(0, 0, 0);
            //弹出窗口，点过赞的背景颜色，蓝色
            java.awt.Color color2 = getColor(iZan, "background-color");
            String zanNum = iZan.getText();
            System.out.println("********** 没点过赞。弹窗赞，赞前数值:" + zanNum + ", 赞前颜色:" + color2.toString());

            try {
                iZan.click();
                sleep(1000);
                iZan.click();
                sleep(1000);
                iZan.click();
                sleep(1000);
                iZan.click();
                sleep(1000);
                iZan.click();
                sleep(1000);

                //刷新再赞
                driver.navigate().refresh();
                sleep(3000);
                waitForPageLoad(driver);
                waitForElement(driver, By.xpath(zanPath));
            } catch (Exception e) {
                e.printStackTrace();
            }

            iZan = driver.findElement(By.xpath(zanPath));
            //===================
            //printWebElement(iZan);
            //===================

            color2 = getColor(iZan, "background-color"); //赞后背景颜色
            String afterZan = iZan.getText();
            System.out.println("********** 没点过赞。弹窗赞，赞后数值:" + afterZan + ", 赞后颜色:" + color2.toString());

            int count = 0;
            while (zanNum.equals(afterZan) || color1.equals(color2)) {
                count++;
                if (count >  3) Assert.fail("timeout");
                System.out.println("------------- 弹窗点赞不成功，循环点赞，赞前数值:" + afterZan + ", 赞前颜色:" + color2.toString());
                try {
                    iZan = driver.findElement(By.xpath(zanPath));
                    iZan.click();
                    sleep(1000);
                    iZan.click();
                    sleep(1000);
                    iZan.click();
                    sleep(1000);
                    iZan.click();
                    sleep(1000);
                    iZan.click();
                    sleep(1000);

                    color2 = getColor(iZan, "background-color"); //赞后背景颜色
                    afterZan = iZan.getText();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("------------- 弹窗点赞不成功，循环点赞，赞后数值:" + afterZan + ", 赞后颜色:" + color2.toString());
            }


            //评论内容
            driver.findElement(By.id("content")).click();
            driver.findElement(By.id("content")).clear();
            if (this.username.substring(0, 3).equals("136")) {
                driver.findElement(By.id("content")).sendKeys("虚心使人进步，点赞积累财富！ " + afterZan);
            } else {
                driver.findElement(By.id("content")).sendKeys("每天学习一点点，每天进步一点点！ " + afterZan);
            }

            //发表按钮
            driver.findElement(By.xpath("//div[@id='root']/div/div/div/div/div[2]/div/div/div[7]/button")).click();
            waitForPageLoad(driver);
            driver.close();
        }

        driver.switchTo().window(fatherWindow);
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


    private static Color getColor(WebElement zan, String col) {
        String color = zan.getCssValue(col);

        if (color.contains("rgba")) {
            color = color.substring(5, color.length() - 1);
        } else {
            color = color.substring(4, color.length() - 1);
        }

        String[] strs = color.split(",");
        for (int i = 0, len = strs.length; i < len; i++) {
//            System.out.println(strs[i].toString());
        }

        java.awt.Color color2 = new java.awt.Color(Integer.parseInt(strs[0].trim()), Integer.parseInt(strs[1].trim()), Integer.parseInt(strs[2].trim()));
        return color2;
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

    private void printWebElement(WebElement iZan) {
        System.out.println("^^^^^^^^^^^^ iZan.toString():" + iZan.toString());
        System.out.println("^^^^^^^^^^^^ iZan.isDisplayed():" + iZan.isDisplayed());
        System.out.println("^^^^^^^^^^^^ iZan.isEnabled():" + iZan.isEnabled());
        System.out.println("^^^^^^^^^^^^ iZan.getCssValue(\"color\"):" + iZan.getCssValue("color"));
        System.out.println("^^^^^^^^^^^^ iZan.getCssValue(\"background\"):" + iZan.getCssValue("background"));
        System.out.println("^^^^^^^^^^^^ iZan.getCssValue(\"background-color\"):" + iZan.getCssValue("background-color"));
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