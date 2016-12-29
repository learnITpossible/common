package com.domain.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * com.domain.common
 * @author Mark Li
 * @version 1.0.0
 * @since 2016/12/29
 */
public class ConcurrentDateUtil {

    private static final Logger logger = LoggerFactory.getLogger(ConcurrentDateUtil.class);

    private static final String dateFormat = "yyyy-MM-dd";

    private static final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";

    private static ThreadLocal<Map<String, DateFormat>> threadLocal = new ThreadLocal<>();

    public static DateFormat getDateFormat(String formatStr) {

        Map<String, DateFormat> map = threadLocal.get();
        DateFormat formatter = map.get(formatStr);
        if (formatter == null) {
            formatter = new SimpleDateFormat(formatStr);
            map.put(formatStr, formatter);
            threadLocal.set(map);
        }
        return formatter;
    }

    public static String formatDate(Date date) {

        return getDateFormat(dateFormat).format(date);
    }

    public static Date parseDate(String dateStr) {

        try {
            return getDateFormat(dateFormat).parse(dateStr);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static String formatDateTime(Date date) {

        return getDateFormat(dateTimeFormat).format(date);
    }

    public static Date parseDateTime(String dateStr) {

        try {
            return getDateFormat(dateTimeFormat).parse(dateStr);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}
