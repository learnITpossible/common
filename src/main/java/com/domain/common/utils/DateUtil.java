package com.domain.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author lvyou
 * @date 2014年9月2日 下午6:19:28
 */
public class DateUtil extends D {

    private static Logger log = LoggerFactory.getLogger(DateUtil.class);

    public static final String DATE_YEAR = "year";

    public static final String DATE_MONTH = "month";

    public static final String DATE_DAY = "day";

    public static final String DAY_OF_MONTH = "dayOfMonth";

    /**
     * hh:mm:ss 24小时制转12小时 AM/PM
     * 获取 h:mm:s a  6:24:48 PM 格式
     * @param date
     * @return
     */
    public static String getStrHmsa(Date date) {

        if (date != null) {
            SimpleDateFormat sdformaTimet = new SimpleDateFormat("h:mm:ss a", Locale.US);
            return sdformaTimet.format(date);
        } else {
            return "";
        }
    }

    /**
     * 获取String  yyyy-MM-dd 日期  可String Date互转
     * @param date
     * @return
     */
    public static String getStrYMD(Date date) {

        if (date != null) {
            SimpleDateFormat sdformatDay = new SimpleDateFormat("yyyy-MM-dd");
            return sdformatDay.format(date);
        } else {
            return "";
        }
    }

    public static String getStrYMDHIS(Date date) {

        if (date != null) {
            SimpleDateFormat sdformatDay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdformatDay.format(date);
        } else {
            return "";
        }
    }

    public static Date getDateYMDHIS(String str) throws Exception {

        if (str != null && !"".equals(str)) {
            SimpleDateFormat sdformatDay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdformatDay.parse(str);
        } else {
            return null;
        }
    }

    /**
     * 获取Date  yyyy-MM-dd 日期 String 转Date
     * @param date
     * @return
     */
    public static Date getStrYMD(String str) throws Exception {

        if (str != null) {
            SimpleDateFormat sdformatDay = new SimpleDateFormat("yyyy-MM-dd");
            return sdformatDay.parse(str);
        } else {
            return null;
        }
    }

    /**
     * yyyy-MM-dd h:mm:ss PM/AM 转Date 类型 24小时制
     * 2014-09-02 6:24:48 PM   转Date 类型
     * @param str
     * @return
     * @throws Exception
     */
    public static Date getStrAPMToDate(String str) throws Exception {

        if (str != null && !"".equals(str)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd h:mm:ss a", Locale.US);
            return sdf.parse(str);
        } else {
            return null;
        }
    }

    public static String getStrFormat(Date date, String formatStr) {

        if (date != null) {
            SimpleDateFormat sdformaTimet = null;
            if (formatStr != null) {
                sdformaTimet = new SimpleDateFormat(formatStr);
                return sdformaTimet.format(date);
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    /**
     * 根据任意时间获取Date
     * @param hour
     * @param minute
     * @param second
     * @param milliSecond
     * @return
     */
    public static String getDate(int hour, int minute, int second, int milliSecond) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.MILLISECOND, milliSecond);
        Date date = new Date(cal.getTimeInMillis());
        try {
            return format.format(date);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;

    }

    public static Map<String, String> getLastWeekTime() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, String> map = new HashMap<String, String>();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, 1);

        map.put("endTime", sdf.format(cal.getTime()));

        cal.add(Calendar.WEEK_OF_MONTH, -1);
        cal.set(Calendar.DAY_OF_WEEK, 2);

        map.put("startTime", sdf.format(cal.getTime()));

        return map;

    }

    /**
     * 获取本周一零点的时间戳
     * @return
     */
    public static Long getThisWeekMondayTime() {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime().getTime();
    }

    /**
     * 获取上周一零点的时间戳
     * @return
     */
    public static Long getLastWeekMondayTime() {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.WEEK_OF_MONTH, -1);
        cal.set(Calendar.DAY_OF_WEEK, 2);
        return cal.getTime().getTime();
    }

    //当前第几周
    public static int getWeekNumber() {

        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(new Date());
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    //根据第N周获取 起始时间
    public static Date getFirstDayInWeek(int week) {

        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改

        int year = c.get(Calendar.YEAR);
        Calendar calFirstDayOfTheYear = new GregorianCalendar(year, Calendar.JANUARY, 1);
        calFirstDayOfTheYear.add(Calendar.DATE, 7 * (week - 1));
        int dayOfWeek = calFirstDayOfTheYear.get(Calendar.DAY_OF_WEEK) - 1;
        Calendar calFirstDayInWeek = (Calendar) calFirstDayOfTheYear.clone();

        calFirstDayInWeek.add(Calendar.DATE, calFirstDayOfTheYear.getActualMinimum(Calendar.DAY_OF_WEEK) - dayOfWeek);
        Date firstDayInWeek = calFirstDayInWeek.getTime();
        //System.out.println(2014 + "年第" + week + "个礼拜的第一天是" +  df.format(firstDayInWeek));
        return firstDayInWeek;
    }

    //根据周 获取本周最后一天日期
    public static Date getLastDayInWeek(int week) {

        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改

        int year = c.get(Calendar.YEAR);
        Calendar calFirstDayOfTheYear = new GregorianCalendar(year, Calendar.JANUARY, 1);
        calFirstDayOfTheYear.add(Calendar.DATE, 7 * (week - 1));
        int dayOfWeek = calFirstDayOfTheYear.get(Calendar.DAY_OF_WEEK) - 1;
        Calendar calLastDayInWeek = (Calendar) calFirstDayOfTheYear.clone();
        calLastDayInWeek.add(Calendar.DATE, calFirstDayOfTheYear.getActualMaximum(Calendar.DAY_OF_WEEK) - dayOfWeek);
        Date lastDayInWeek = calLastDayInWeek.getTime();
        return lastDayInWeek;
    }

    public static List<String> getWeekListStr(int weekNum) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        // int weekNum=getWeekNumber();
        List<String> weekMap = new ArrayList<>();
        for (int i = 1; i <= weekNum; i++) {

            String key = "第" + i + "周(" + df.format(getFirstDayInWeek(i)) + "~" + df.format(getLastDayInWeek(i)) + ")";
            weekMap.add(key);
        }
        return weekMap;
    }

    /**
     * 根据不同period获取两个时间戳之间的时间段
     * @param startTime
     * @param endTime
     * @param period
     * @return
     */
    public static List<Date> getBetweenDate(Long startTime, Long endTime, int period) {

        List<Date> resultList = new ArrayList<Date>();
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        startCalendar.setTimeInMillis(startTime);
        endCalendar.setTimeInMillis(endTime);
        if (period < 7) {//REPORT_PERIOD_hour<7 设置分秒为0
            startCalendar.set(Calendar.MINUTE, 0);
            startCalendar.set(Calendar.SECOND, 0);
            startCalendar.set(Calendar.MILLISECOND, 0);
            endCalendar.set(Calendar.MINUTE, 0);
            endCalendar.set(Calendar.SECOND, 0);
            endCalendar.set(Calendar.MILLISECOND, 0);
        }
        if (period < 6) {//REPORT_PERIOD_hour<6 设置时为0
            startCalendar.set(Calendar.HOUR_OF_DAY, 0);
            endCalendar.set(Calendar.HOUR_OF_DAY, 0);
        }
        if (period == 4) {
            startCalendar.set(Calendar.DAY_OF_WEEK, 2);
            endCalendar.set(Calendar.DAY_OF_WEEK, 2);
        }
        if (period <= 3) {//REPORT_PERIOD_hour<6 设置每月的第一天为1号
            startCalendar.set(Calendar.DAY_OF_MONTH, 1);
            endCalendar.set(Calendar.DAY_OF_MONTH, 1);
        }
        if (period == 2) {//REPORT_PERIOD_hour=2  设置结尾时间值为最近的季度月
            int mod = 0;
            mod = (endCalendar.get(Calendar.MONTH) + 1) % 3;
            if (mod == 0) {
                mod = 2;
            } else {
                mod = mod - 1;
            }
            endCalendar.add(Calendar.MONTH, -mod);
        }
        if (period == 1) { //REPORT_PERIOD_hour=1  设置每年第一月份为1月
            startCalendar.set(Calendar.MONTH, 0);
            endCalendar.set(Calendar.MONTH, 0);
        }

        long endTimeInMillis = endCalendar.getTimeInMillis();
        if (period == 6) {//返回以小时为单位的List<Date>
            while (true) {
                startCalendar.add(Calendar.HOUR, 1);
                if (startCalendar.getTimeInMillis() < endTimeInMillis) {
                    resultList.add(startCalendar.getTime());
                } else {
                    break;
                }
            }
        }
        if (period == 5) {//返回以天为单位的List<Date>
            while (true) {
                startCalendar.add(Calendar.DAY_OF_YEAR, 1);
                if (startCalendar.getTimeInMillis() < endTimeInMillis) {
                    resultList.add(startCalendar.getTime());
                } else {
                    break;
                }
            }
        }
        if (period == 4) {//返回以周为单位的List<Date>
            while (true) {
                startCalendar.add(Calendar.WEEK_OF_YEAR, 1);
                if (startCalendar.getTimeInMillis() < endTimeInMillis) {
                    resultList.add(startCalendar.getTime());
                } else {
                    break;
                }
            }
        }
        if (period == 3) {//返回以月为单位的List<Date>
            while (true) {
                startCalendar.add(Calendar.MONTH, 1);

                if (startCalendar.getTimeInMillis() < endTimeInMillis) {
                    resultList.add(startCalendar.getTime());
                } else {
                    break;
                }
            }
        }
        if (period == 2) {//返回以季度月为单位的List<Date>
            while (true) {
                int mod = 0;
                mod = (startCalendar.get(Calendar.MONTH) + 1) % 3;
                if (mod == 0) {
                    mod = 1;
                } else {
                    mod = 4 - mod;
                }
                startCalendar.add(Calendar.MONTH, mod);
                if (startCalendar.getTimeInMillis() < endTimeInMillis) {
                    resultList.add(startCalendar.getTime());
                } else {
                    break;
                }
            }
        }
        if (period == 1) {//返回以年为单位的List<Date>
            while (true) {
                startCalendar.add(Calendar.YEAR, 1);
                if (startCalendar.getTimeInMillis() < endTimeInMillis) {
                    resultList.add(startCalendar.getTime());
                } else {
                    break;
                }
            }
        }
        return resultList;
    }

    /**
     * 日期相加  days天数
     * @param date
     * @param days
     * @return
     */
    public static Date getAddDate(Date date, int days) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + days);
        return calendar.getTime();
    }

    /**
     * 日期减    days 天数
     * @param date
     * @param days
     * @return
     */
    public static Date getReduceDate(Date date, int days) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - days);
        return calendar.getTime();

    }

    /**
     * 日期减    days 天数
     * @param date
     * @param days
     * @return
     */
    public static Date getLastNDate(Date date, int days) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - days);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 日期减  week周数
     * @param date
     * @param days
     * @return
     */
    public static Date getLastNWeek(Date date, int week) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.WEEK_OF_MONTH, -week);
        cal.set(Calendar.DAY_OF_WEEK, 1);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();

    }

    /**
     * 日期减   month月数
     * @param date
     * @param days
     * @return
     */
    public static Date getLastNMonth(Date date, int month) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 用户追踪专用，获取12个period之前的起始日
     * @param date
     * @param period
     * @return
     */
    public static Date getStartPeriod(Date date, Integer period) {

        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        dateCalendar.set(Calendar.MINUTE, 0);
        dateCalendar.set(Calendar.SECOND, 0);
        dateCalendar.set(Calendar.MILLISECOND, 0);
        dateCalendar.set(Calendar.HOUR_OF_DAY, 0);
        if (period == 3) {
            dateCalendar.set(Calendar.DAY_OF_MONTH, 1);
            dateCalendar.add(Calendar.MONTH, -13);
        } else if (period == 4) {
            dateCalendar.add(Calendar.WEEK_OF_YEAR, -13);
            dateCalendar.set(Calendar.DAY_OF_WEEK, 1);
        }
        return dateCalendar.getTime();
    }

    /**
     * 日期减   month月数
     * @param date
     * @param days
     * @return
     */
    public static Calendar getLastNMonthCal(Date date, int month) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * 计算日期差  年份、月份、天数
     * @param startDate
     * @param endDate
     * @param type
     * @return
     */
    public static int getDelDate(String startDate, String endDate, String type) {

        Date st;
        Date et;
        int countMonth = 0;//这个是用来计算得到有多少个月时间的一个整数,
        try {
            st = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
            et = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
            Calendar ca1 = Calendar.getInstance();
            Calendar ca2 = Calendar.getInstance();
            ca1.setTime(st);
            ca2.setTime(et);

            int ca1Year = ca1.get(Calendar.YEAR);
            int ca1Month = ca1.get(Calendar.MONTH);
            int calDay = ca1.get(Calendar.DAY_OF_MONTH);

            int ca2Year = ca2.get(Calendar.YEAR);
            int ca2Month = ca2.get(Calendar.MONTH);
            int ca2Day = ca2.get(Calendar.DAY_OF_MONTH);

            switch (type) {
                case DATE_YEAR:
                    countMonth = ca2Year - ca1Year;
                    break;
                case DATE_MONTH:
                    if (ca1Year == ca2Year) {
                        countMonth = ca2Month - ca1Month;
                    } else {
                        countMonth = (ca2Year - ca1Year) * 12 + (ca2Month - ca1Month);
                    }
                    break;
                case DATE_DAY:
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                    Long c = sf.parse(startDate).getTime() - sf.parse(endDate).getTime();
                    countMonth = (int) (c / 1000 / 60 / 60 / 24);
                    break;
                case DAY_OF_MONTH:
                    countMonth = ca2Day - calDay;
                    break;
                default:
                    break;
            }
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return countMonth;
    }

    /**
     * 格式化时间
     * @param date
     * @param pattern
     * @return
     */
    public static String format(String date, String pattern) throws Exception {

        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date d = format.parse(date);
        return format.format(d);
    }

    public static String format(Object date, String pattern) throws Exception {

        Date d = (Date) date;
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(d);
    }

    public static String format(String date) throws Exception {

        return format(date, "yyyy-MM-dd");
    }

    /**
     * 获取前一天日期
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static String getSpecifiedDayBefore(String dateStr) throws ParseException {

        Calendar cal = Calendar.getInstance();
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        cal.setTime(date);
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day - 1);
        String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

        return dayBefore;
    }
}
