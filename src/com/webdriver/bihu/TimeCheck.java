package com.webdriver.bihu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeCheck {
    public static void main(String[] args) throws ParseException {
        String style = "HH:mm:ss";
        SimpleDateFormat df = new SimpleDateFormat(style);

        Date startTime = new SimpleDateFormat(style).parse("19:00:00");
        Date nowTime = new SimpleDateFormat(style).parse(df.format(new Date()));
        Date endTime = new SimpleDateFormat(style).parse("19:01:59");
        System.out.println(isEffectiveDate(nowTime, startTime, endTime));
    }

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime   当前时间
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     * @author jqlin
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }
}
