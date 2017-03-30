package com.domain.common.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yanxuegao
 */
public class Vacations {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Vacations.class);

    private static int START_YEAR = 2015;

    private static int END_YEAR = 2020;

    private static Year[] YEARS = new Year[10];

    Calendar start = Calendar.getInstance();

    Calendar end = Calendar.getInstance();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 计算两个日期之间的工作日个数（包含起始时间，不包含结束时间，月份从1开始）
     * @param startStr yyyy-MM-dd
     * @param endStr   yyyy-MM-dd
     * @return 返回两个日期之间的工作日天数（包含起始时间，不包含结束时间，月份从1开始）
     * @throws ParseException
     */
    public int countWorkDay(String startStr, String endStr) throws ParseException {

        start.clear();
        start.setTime(sdf.parse(startStr));

        end.clear();
        end.setTime(sdf.parse(endStr));

        return countWorkDay(start, end);
    }

    /**
     * @param start
     * @param end
     * @return
     */
    private int countWorkDay(Calendar start, Calendar end) {

        int workDayCount = 0;
        while (start.before(end)) {
            workDayCount += Vacations.上班(start) ? 1 : 0;
            start.add(Calendar.DATE, 1);
        }
        return workDayCount;
    }

    /**
     * 周一至周五上班或周未休息则返回true 否则返回false
     * @param calendar
     * @return
     */
    public static boolean isNormalDay(Calendar calendar) {

        int yearIndex = calendar.get(Calendar.YEAR) - START_YEAR;
        int monthIndex = calendar.get(Calendar.MONTH);
        int dayIndex = calendar.get(Calendar.DATE);

        boolean isNormalDay = false;
        if (yearIndex < 0 || yearIndex > END_YEAR - START_YEAR || yearIndex + 1 > YEARS.length)
            isNormalDay = true;

        if (isNormalDay
                || Vacations.YEARS[yearIndex] == null
                || Vacations.YEARS[yearIndex].MONTHS[monthIndex] == null
                || Vacations.YEARS[yearIndex].MONTHS[monthIndex].DAYS.get(dayIndex) == null) {
            isNormalDay = true;
        }
        return isNormalDay;
    }

    /**
     * @param calendar
     * @return calendar 所指当天是否上班，上班则返回true 反之返回false
     */
    public static boolean 上班(Calendar calendar) {

        int yearIndex = calendar.get(Calendar.YEAR);
        yearIndex -= START_YEAR;
        int monthIndex = calendar.get(Calendar.MONTH);
        int dayIndex = calendar.get(Calendar.DATE);
        int dayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 + 1; // 从1 开始算，周一为每周第一天

        if (isNormalDay(calendar)) {
            return dayOfWeek < 6; // 正常工作日上班
        } else {
            Day day = Vacations.YEARS[yearIndex].MONTHS[monthIndex].DAYS.get(dayIndex);
            return day.调班();
        }
    }

    /**
     * @param c
     * @return calendar 所指当天是否休假，休假则返回true 反之返回false
     */
    public static boolean 休假(Calendar c) {

        int yearIndex = c.get(Calendar.YEAR) - START_YEAR;
        int monthIndex = c.get(Calendar.MONTH);
        int dayIndex = c.get(Calendar.DATE);
        int dayOfWeek = (c.get(Calendar.DAY_OF_WEEK) + 5) % 7 + 1;// 从1 开始算，周一为每周第一天

        boolean isNormalDay = isNormalDay(c);

        if (isNormalDay) {
            return dayOfWeek >= 6;// 正常周未休息
        } else {
            Day day = Vacations.YEARS[yearIndex].MONTHS[monthIndex].DAYS.get(dayIndex);
            return day.放假();
        }
    }

    static class Year {

        public Month[] MONTHS = new Month[12];
    }

    static class Month {

        public Map<Integer, Day> DAYS;

    }

    static class Day {

        int year;

        int month;

        int day;

        int type;// 1-工作日放假，2-周未调班

        public Day() {

        }

        public Day(int year, int month, int day, int type) {

            this.year = year;
            this.month = month;
            this.day = day;
            this.type = type;
        }

        public boolean 放假() {

            return type == 1;
        }

        public boolean 调班() {

            return type == 2;
        }
    }

    /**
     * 从配置文件读取放假安排时间表
     * 文件格式：
     * yyyy-MM-dd-上班
     * yyyy-MM-dd-放假
     * @throws Exception
     */
    static void initVacations() throws Exception {

        String filename = "vacations.txt";
        InputStream is = null;
        String sys_dir = null;

        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            sys_dir = System.getProperty("user.home") + "/conf/";
        } else {
            sys_dir = "/opt/conf/";
        }
        File hashFile = new File(sys_dir + filename);
        if (hashFile.exists()) {
            is = new FileInputStream(hashFile);
            logger.info("load vacations config file: " + hashFile.getPath());
        } else {
            RuntimeException e = new RuntimeException(
                    "vacations config file(" + hashFile.getPath() + ") load failed! use default");
            if (logger.isWarnEnabled()) {
                logger.warn(e.getMessage(), e);
            } else {
                e.printStackTrace();
            }
            is = Vacations.class.getClassLoader().getResourceAsStream(filename);
            if (is == null) {
                logger.warn("Load default vacations config file(" + filename + ") failed! ");
                return;
            }
        }

        BufferedReader sr = new BufferedReader(new InputStreamReader(is));
        try {
            String line;
            while ((line = sr.readLine()) != null) {
                if (StringUtils.isBlank(line) || line.startsWith("#")) break;    //忽略空行，注释行
                String[] dayArry = line.split("-");
                int yearIndex = Integer.valueOf(dayArry[0]) - Vacations.START_YEAR;

                if (Vacations.YEARS[yearIndex] == null) {
                    Vacations.YEARS[yearIndex] = new Year();    //年份索引从0开始
                }

                int monthIndex = Integer.valueOf(dayArry[1]) - 1; //月份索引从0开始

                if (Vacations.YEARS[yearIndex].MONTHS[monthIndex] == null) {
                    Vacations.YEARS[yearIndex].MONTHS[monthIndex] = new Month();
                }

                if (Vacations.YEARS[yearIndex].MONTHS[monthIndex].DAYS == null) {
                    Vacations.YEARS[yearIndex].MONTHS[monthIndex].DAYS = new HashMap<Integer, Day>();
                }

                int dayType = "R".equals(dayArry[3]) ? 1 : 2;
                Vacations.YEARS[yearIndex].MONTHS[monthIndex].DAYS.put(Integer.valueOf(dayArry[2]),
                        new Day(Integer.valueOf(dayArry[0]), Integer.valueOf(dayArry[1]), Integer.valueOf(dayArry[2]),
                                dayType));
            }
        } finally {
            sr.close();
        }
    }

    static {
        try {
            synchronized (YEARS) {
                initVacations();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws ParseException {

        Vacations v = new Vacations();
        System.out.println(v.countWorkDay("2015-01-01", "2017-01-01"));
        System.out.println(v.countWorkDay("2015-02-01", "2017-02-01"));
        System.out.println(v.countWorkDay("2016-10-01", "2016-10-31"));
    }
}
