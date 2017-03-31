package com.domain.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadUtil {

    private static Logger log = LoggerFactory.getLogger(ThreadUtil.class);

    public static void sleep(long millis) {

        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
