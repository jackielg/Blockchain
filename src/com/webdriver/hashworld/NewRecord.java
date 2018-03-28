package com.webdriver.hashworld;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

public class NewRecord {
    private WebDriver driver;
    private String baseUrl;
    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        baseUrl = "https://www.katalon.com/";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
    public void testUntitledTestCase() throws Exception {
        driver.get("https://game.hashworld.top/#!/login");
        driver.findElement(By.id("logintel")).click();
        driver.findElement(By.id("logintel")).clear();
        driver.findElement(By.id("logintel")).sendKeys("13601223469");
        driver.findElement(By.id("loginpassword")).click();
        driver.findElement(By.id("loginpassword")).clear();
        driver.findElement(By.id("loginpassword")).sendKeys("Liuxb0504");
        driver.findElement(By.id("loginsubmit")).click();


        driver.findElement(By.xpath("//hw-treasure-block/div/img")).click();
        driver.findElement(By.xpath("//div[3]/div[2]/div")).click();
        driver.findElement(By.xpath("//button")).click();

        driver.findElement(By.xpath("//hw-treasure-block[2]/div/img")).click();
        driver.findElement(By.xpath("//div[3]/div[2]/div")).click();
        driver.findElement(By.xpath("//button")).click();

        driver.findElement(By.xpath("//hw-treasure-block[3]/div/img")).click();
        driver.findElement(By.xpath("//div[3]/div[2]/div")).click();
        driver.findElement(By.xpath("//button")).click();

        driver.findElement(By.xpath("//div[3]/img")).click();
        driver.findElement(By.xpath("//hw-my/div[2]/div")).click();
        driver.findElement(By.xpath("//button")).click();
    }

    @After
    public void tearDown() throws Exception {
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
}



