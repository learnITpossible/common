package com.domain.common.utils;

import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class D {

    public static final String DEFAULT_PARTTERN = "yyyy-MM-dd HH:mm:ss";

    // formatDate("yyyyMMdd HH:mm:ss", new Date());
    public static String formatDate(String pattern, Date date) {

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    // yyyy-MM-dd HH:mm:ss
    // "EEE, d MMM yyyy HH:mm:ss" => Wed, 4 Jul 2001 12:08:56
    public static String unixtime2string(long mills, String... pattern) {

        String _pattern = pattern.length > 0 ? pattern[0] : DEFAULT_PARTTERN;
        String result = null;
        try {
            Date d = (new Date(mills));
            SimpleDateFormat sdf = new SimpleDateFormat(_pattern);
            Calendar c1 = Calendar.getInstance();
            c1.setTime(d);
            result = sdf.format(c1.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String unixtime2string(int sec, String... pattern) {

        return unixtime2string(sec * 1000L, pattern);
    }

    public static String formatDate() {

        return formatDate(DEFAULT_PARTTERN);
    }

    public static String formatDate(String pattern) {

        return formatDate(pattern, new Date());
    }

    public static int unixTime() {

        long mills = new Date().getTime();
        return (int) (mills / 1000);
    }

    public static long toTimeStamp(String myDate, String... pattern) {

        String _pattern = pattern.length > 0 ? pattern[0] : DEFAULT_PARTTERN;
        SimpleDateFormat sdf = new SimpleDateFormat(_pattern);
        try {
            Date time = sdf.parse(myDate);
            return time.getTime();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Date getWeekStart(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DATE, 1 - dayOfWeek);
        return cal.getTime();
    }

    public static Date getWeekEnd(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DATE, 7 - dayOfWeek);
        return cal.getTime();
    }

    public static Date addMinute(Date date, int amount) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, amount);
        return cal.getTime();
    }

    public static Date addSecond(Date date, int amount) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, amount);
        return cal.getTime();
    }

    public static Date addDate(Date date, int amount) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, amount);
        return cal.getTime();
    }

    public static Date addMonth(Date date, int amount) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, amount);
        return cal.getTime();
    }

    public static boolean diffDate(int time1, int time2) {

        String s1 = D.unixtime2string(time1, "yyyyMMdd");
        String s2 = D.unixtime2string(time2, "yyyyMMdd");
        return s1.equals(s2);
    }

    public static Date getDate(String s_date) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(s_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*******************************
     * 从WBOT迁移过来的方法，做了调整
     *******************************/

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyyMMddhhmmss");

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT2 = new SimpleDateFormat("yyyy-MM-dd");

    private static final DateFormat DATETIME_FORMAT = DateFormat.getDateTimeInstance();

    private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance();

    public static Date ParseDateTime(String s) throws ParseException {

        return DATETIME_FORMAT.parse(s);
    }

    public static Date ParseDate(String s) throws ParseException {

        return DATE_FORMAT.parse(s);
    }

    public static String currDateTimeStr() {

        synchronized (SIMPLE_DATE_FORMAT) {
            return SIMPLE_DATE_FORMAT.format(new Date());
        }
    }

    public static String currMonthStartStr() {

        synchronized (SIMPLE_DATE_FORMAT2) {
            Date d = new Date();
            d.setDate(1);
            return SIMPLE_DATE_FORMAT2.format(d);
        }
    }

    public static Date normalDateTimeStr(String strDate) throws ParseException {

        return normalDateTimeStr(null, strDate);
    }

    public static Date normalDateTimeStr(String patten, String strDate) throws ParseException {

        if (StringUtils.isBlank(patten)) {
            patten = DEFAULT_PARTTERN;
        }
        synchronized (patten) {
            SimpleDateFormat sdf = new SimpleDateFormat(patten);
            return sdf.parse(strDate);
        }
    }

    public static String lastMonthStartStr() {

        synchronized (SIMPLE_DATE_FORMAT2) {
            Date d = new Date();
            d.setMonth(d.getMonth() - 1);
            d.setDate(1);
            return SIMPLE_DATE_FORMAT2.format(d);
        }
    }

    public static String TimespanToString(long l) {

        int minute = (int) (l / 1000 / 60);
        int hour = minute / 60;
        if (hour > 0) {
            return String.format("%d小时%2d分", hour, minute % 60);
        }
        return String.format("%2d分", minute);
    }

    public static Timestamp begin_date;

    static {
        try {
            begin_date = new Timestamp(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2001-01-01 00:00:00").getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public static String TimeToString(Date ts) {

        if (ts.compareTo(begin_date) < 0)
            return "-";
        return ts.toLocaleString();
    }

    public static String TimeToString(long l) {

        return TimeToString(new Date(l));
    }

    /**
     * @param sec, unit: seconds
     * @return
     */
    public static String formatTime(int sec) {

        int ss = 0, mm = 1, HH = 0;
        int leftSec = sec;
        ss = leftSec % 60;
        leftSec = leftSec / 60;
        mm = leftSec % 60;

        leftSec = leftSec / 60;
        HH = leftSec % 60;
        StringBuffer sb = new StringBuffer();
        if (HH > 0) {
            sb.append(HH).append("小时");
        }
        // 大于30秒算1分钟
        if (ss > 30) {
            mm++;
        }
        // 小于1分钟算1分钟
        if (HH == 0 && mm <= 0) {
            mm = 1;
        }
        if (mm > 0) {
            sb.append(mm).append("分钟");
        }
        return sb.toString();
    }

}

