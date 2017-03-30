package com.domain.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ThreadUtil {

    private static Log log = LogFactory.getLog(ThreadUtil.class);

    public static void sleep(long millis) {

        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
