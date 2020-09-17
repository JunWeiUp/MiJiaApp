package com.handongkeji.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.text.format.Time;

import com.handongkeji.common.SystemHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;


public class DateUtil {


    public static String getTimeString(int hourOfDay, int minute) {
        String half = "";
        if (hourOfDay < 6) {
            half = "凌晨";
        } else if (hourOfDay < 12) {
            half = "上午";
        } else if (hourOfDay < 18) {
            half = "下午";
        } else if (hourOfDay < 24) {
            half = "晚上";
        }

        hourOfDay = hourOfDay % 12;
        return String.format("%s%s:%s", half, hourOfDay, minute);
    }

    public static int getAgeByBirthDay(long userbirthday) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(userbirthday);
        int year = instance.get(Calendar.YEAR);

        int year1 = Calendar.getInstance().get(Calendar.YEAR);
        int age = year1 - year;
        return age;
    }

    public static String getTimeByStartEndTime(long helpendtime, long helpstarttime) {
        String yzTime = SystemHelper.getTimeString(helpstarttime) + "-" + SystemHelper.getTimeString(helpendtime);
        return yzTime;
    }


    public static String formatDateTime(long dateTime) {
        String text;

        Date date = new Date();
        date.setTime(dateTime);

        if (isSameDay(dateTime)) {
            Calendar calendar = GregorianCalendar.getInstance();
            if (inOneMinute(dateTime, calendar.getTimeInMillis())) {
                return "刚刚";
            } else if (inOneHour(dateTime, calendar.getTimeInMillis())) {
                return String.format("%d分钟之前", Math.abs(dateTime - calendar.getTimeInMillis()) / 60000);
            } else {
                calendar.setTime(date);
                int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                if (hourOfDay > 17) {
                    text = "晚上 hh:mm";
                } else if (hourOfDay >= 0 && hourOfDay <= 6) {
                    text = "凌晨 hh:mm";
                } else if (hourOfDay > 11 && hourOfDay <= 17) {
                    text = "下午 hh:mm";
                } else {
                    text = "上午 hh:mm";
                }
            }
        } else if (isYesterday(dateTime)) {
            text = "昨天 HH:mm";
        } else if (isSameYear(dateTime)) {
            text = "M月d日 HH:mm";
        } else {
            text = "yyyy-M-d HH:mm";
        }

        // 注意，如果使用android.text.format.DateFormat这个工具类，在API 17之前它只支持adEhkMmszy
        return new SimpleDateFormat(text, Locale.CHINA).format(dateTime);
    }

    private static boolean inOneMinute(long time1, long time2) {
        return Math.abs(time1 - time2) < 60000;
    }

    private static boolean inOneHour(long time1, long time2) {
        return Math.abs(time1 - time2) < 3600000;
    }

    private static boolean isSameDay(long time) {
        long startTime = floorDay(Calendar.getInstance()).getTimeInMillis();
        long endTime = ceilDay(Calendar.getInstance()).getTimeInMillis();
        return time > startTime && time < endTime;
    }

    private static boolean isYesterday(long time) {
        Calendar startCal;
        startCal = floorDay(Calendar.getInstance());
        startCal.add(Calendar.DAY_OF_MONTH, -1);
        long startTime = startCal.getTimeInMillis();

        Calendar endCal;
        endCal = ceilDay(Calendar.getInstance());
        endCal.add(Calendar.DAY_OF_MONTH, -1);
        long endTime = endCal.getTimeInMillis();
        return time > startTime && time < endTime;
    }

    private static boolean isSameYear(long time) {
        Calendar startCal;
        startCal = floorDay(Calendar.getInstance());
        startCal.set(Calendar.MONTH, Calendar.JANUARY);
        startCal.set(Calendar.DAY_OF_MONTH, 1);
        return time >= startCal.getTimeInMillis();
    }

    private static Calendar floorDay(Calendar startCal) {
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);
        return startCal;
    }

    private static Calendar ceilDay(Calendar endCal) {
        endCal.set(Calendar.HOUR_OF_DAY, 23);
        endCal.set(Calendar.MINUTE, 59);
        endCal.set(Calendar.SECOND, 59);
        endCal.set(Calendar.MILLISECOND, 999);
        return endCal;
    }


    /*
     * 获取当前时间�?�?7天的日期和星�?
     */
    @SuppressLint("SimpleDateFormat")
    public static String getWeekDayStr(int i) {
        ArrayList<String> weekDays = new ArrayList<String>();
        // 定义输出日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("M月dd�?" + "\nE");
        Date currentDate = new Date();
        List<Date> days = dateToWeek(currentDate);
        for (Date date : days) {
            weekDays.add(sdf.format(date).replace("�?", "星期"));
        }
        String dayStr;
        switch (i) {
            // 第一天（当前日期，今天），直接返回今天的日期和星�?
            case 1:
                dayStr = sdf.format(currentDate).replace("�?", "星期");
                return dayStr;
            case 2:
                dayStr = weekDays.get(0);
                return dayStr;
            case 3:
                dayStr = weekDays.get(1);
                return dayStr;
            case 4:
                dayStr = weekDays.get(2);
                return dayStr;
            case 5:
                dayStr = weekDays.get(3);
                return dayStr;
            case 6:
                dayStr = weekDays.get(4);
                return dayStr;
            case 7:
                dayStr = weekDays.get(5);
                return dayStr;
        }
        return null;
    }

    /*
     * 根据日期获得�?在周的日�? 
     */
    public static List<Date> dateToWeek(Date mdate) {
        Date fdate;
        List<Date> list = new ArrayList<Date>();
        Long fTime = mdate.getTime();// - b * 24 * 3600000;
        for (int a = 1; a < 7; a++) {
            fdate = new Date();
            fdate.setTime(fTime + (a * 24 * 3600000));
            list.add(a - 1, fdate);
        }
        return list;
    }

    /*
     * 获取今天及后6天的日期，格式为 7�?28�? 星期�? 
     */
    public static ArrayList<String> getWeekDayStrArr() {
        ArrayList<String> weekDayStrArr = new ArrayList<String>();
        for (int i = 1; i <= 7; i++) {
            weekDayStrArr.add(getWeekDayStr(i));
        }
        return weekDayStrArr;
    }

    /*
     * 获取今天及后6天的日期，格式为 2014-07-28
     */
    public static ArrayList<String> getWeekDate() {
        ArrayList<String> weekDateArr = new ArrayList<String>();
        for (int i = 1; i <= 7; i++) {
            weekDateArr.add(getWeekDateStr(i));
        }
        return weekDateArr;
    }

    /*
     * 获取当前时间�?�?7天的日期和星�?
     */
    @SuppressLint("SimpleDateFormat")
    public static String getWeekDateStr(int i) {
        ArrayList<String> weekDays = new ArrayList<String>();
        // 定义输出日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        List<Date> days = dateToWeek(currentDate);
        for (Date date : days) {
            weekDays.add(sdf.format(date));
        }
        String dayStr;
        switch (i) {
            // 第一天（当前日期，今天），直接返回今天的日期和星�?
            case 1:
                dayStr = sdf.format(currentDate);
                return dayStr;
            case 2:
                dayStr = weekDays.get(0);
                return dayStr;
            case 3:
                dayStr = weekDays.get(1);
                return dayStr;
            case 4:
                dayStr = weekDays.get(2);
                return dayStr;
            case 5:
                dayStr = weekDays.get(3);
                return dayStr;
            case 6:
                dayStr = weekDays.get(4);
                return dayStr;
            case 7:
                dayStr = weekDays.get(5);
                return dayStr;
        }
        return null;
    }

    /*
     * 获取目前年份
     */
    public static int getYear() {
        Time time = new Time("GMT+8");
        time.setToNow();
        int year = time.year;
        return year;
    }

    /*
     * 获取目前月份
     */
    public static int getMonth() {
        Time time = new Time("GMT+8");
        time.setToNow();
        int month = time.month + 1;
        return month;
    }

    /*
     * 获取目前�?
     */
    public static int getDay() {
        Time time = new Time("GMT+8");
        time.setToNow();
        int day = time.monthDay;
        return day;
    }

    /*
     * 获取目前�?
     */
    public static int getHour() {
        Time time = new Time("GMT+8");
        time.setToNow();
        int hour = time.hour + 8;
        return hour;
    }

    /*
     * 获取目前�?
     */
    public static int getMinute() {
        Time time = new Time("GMT+8");
        time.setToNow();
        int minute = time.minute;
        return minute;
    }

    /*
     * 获取目前�?
     */
    public static int getSec() {
        Time time = new Time("GMT+8");
        time.setToNow();
        int sec = time.second;
        return sec;
    }

    /**
     * "yyyy-MM-dd"
     *
     * @param mils
     * @return
     */


    public static String getYmd(long mils) {

        Date date = new Date(mils);
        return getYmd(date);
    }

    public static SimpleDateFormat DF_CENTER_LINE = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    public static SimpleDateFormat DF_WORD = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);

    public static SimpleDateFormat DF_COMMA = new SimpleDateFormat("yyyy.MM.dd", Locale.CHINA);

    public static String getYmd(Date date) {
        return DF_CENTER_LINE.format(date);
    }


    public static String get_year_month_day(long time) {
        return DF_CENTER_LINE.format(new Date(time));
    }

    /**
     * 获取日期 yyyy年MM月dd日 格式
     *
     * @param date
     * @return
     */
    public static String getDateString(Date date) {

        return DF_WORD.format(date);
    }

    /**
     * 将2017-8-5转化为对应的时间long值
     *
     * @param timeStr
     * @return
     */
    public static long getTimeLong(String timeStr) {

        try {
            return DF_CENTER_LINE.parse(timeStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static String getTimeStr(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(new Date(time));
    }

    public static String getTimeMonth(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        return sdf.format(new Date(time));
    }

    public static String getMusic(String time) {
        String mTime = "";
        if (!TextUtils.isEmpty(time) && !time.contains(":")) {
            long l = Long.parseLong(time);
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
            mTime = sdf.format(new Date(l));
        } else {
            mTime = time.substring(3, time.length());
        }
        return mTime;
    }

    public static final SimpleDateFormat DF_POINT = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.CHINA);


    public static String getTimeStr(Date date, SimpleDateFormat sdf) {
        return sdf.format(date);
    }


    public static String getCommaYMD(long time) {


        return DF_COMMA.format(new Date(time));
    }


    public static String getTimeDetail(long time) {
        return DF_POINT.format(new Date(time));
    }

}
