package com.domain.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 线程安全
 * @author Mark Li
 * @version 1.0.0
 * @since 2016/12/21
 */
public class DateUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    private static final SimpleDateFormat 年月日 = new SimpleDateFormat("yyyy-MM-dd");

    private static final SimpleDateFormat 时分秒 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final SimpleDateFormat STANDARD = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);

    public static String formatDate(Date date) {

        synchronized (年月日) {
            return 年月日.format(date);
        }
    }

    public static Date parseDate(String dateStr) {

        synchronized (年月日) {
            try {
                return 年月日.parse(dateStr);
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
                return null;
            }
        }
    }

    public static String formatDateTime(Date date) {

        synchronized (时分秒) {
            return 时分秒.format(date);
        }
    }

    public static Date parseDateTime(String dateStr) {

        synchronized (时分秒) {
            try {
                return 时分秒.parse(dateStr);
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
                return null;
            }
        }
    }

    public static Date getUTCTime(long milliseconds) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        int zoneOffset = calendar.get(Calendar.ZONE_OFFSET);
        int dstOffset = calendar.get(Calendar.DST_OFFSET);
        calendar.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        return calendar.getTime();

    }

    public static void main(String[] args) {

        System.out.println(STANDARD.format(new Date(1481731200000L)));
        System.out.println(STANDARD.format(getUTCTime(1481731200000L)));

        String dateStr = STANDARD.format(new Date(1481731200000L));
        System.out.println(dateStr.replace("+", "UTC$2B"));

    }
}
