package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ouyangshen on 2016/9/24.
 * Modified by RiddleLi on 2018/6/3.
 */
public class DateUtil {
    public static String getNowDateTime(String formatStr) {
        String format = formatStr;
        if (format==null || format.length()<=0) {
            format = "yyyyMMddHHmmss";
        }
        SimpleDateFormat s_format = new SimpleDateFormat(format);
        return s_format.format(new Date());
    }

    public static String getNowTime() {
        SimpleDateFormat s_format = new SimpleDateFormat("HH:mm:ss");
        return s_format.format(new Date());
    }

    public static String getNowTimeDetail() {
        SimpleDateFormat s_format = new SimpleDateFormat("HH:mm:ss.SSS");
        return s_format.format(new Date());
    }

    public static long getDeltaDate(String str) {
        SimpleDateFormat s_format = new SimpleDateFormat("yyyyMMddHHmmss");
        String str2 = getNowDateTime("yyyyMMddHHmmss");
        Date old_date = null, new_date = null;
        try {
            old_date = s_format.parse(str);
            new_date = s_format.parse(str2);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        long old_time = old_date.getTime();
        long new_time = new_date.getTime();
        long diff_time = new_time - old_time;
        long diff_days = diff_time / 24 / 60 / 60 / 1000;
        return diff_days;
    }

    public static long getDeltaDate(String str1, String str2) {
        //注意是后面的减去前面的
        SimpleDateFormat s_format = new SimpleDateFormat("yyyyMMdd");
        Date old_date = null, new_date = null;
        try {
            old_date = s_format.parse(str1);
            new_date = s_format.parse(str2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long old_time = old_date.getTime();
        long new_time = new_date.getTime();
        long diff_time = new_time - old_time;
        return diff_time / 24 / 60 / 60 / 1000;
    }

    public static String getTimeStamp() {
        long time = new Date().getTime();
        time /= 1000;
        return Long.toString(time);
    }

    public static int getDayOfWeek() {
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        if(weekday == 1) {
            weekday = 7;
        } else {
            weekday--;
        }
        return weekday;
    }

    public static int getDaysByYearMonth(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        return a.get(Calendar.DATE);
    }

    public static void main(String[] args) {
        //String s = "202002";
        //System.out.println(getDaysByYearMonth(Integer.parseInt(s.substring(0, 4)), Integer.parseInt(s.substring(4))));
        //System.out.println(getDayOfWeek());
        System.out.println(getDeltaDate("20200228", "20200301"));
    }
}
