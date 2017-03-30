package com.domain.common.utils;

public class ExpUtil {

    public static Throwable getCauseThrowable(Exception e) {

        Throwable causeThrowable = e.getCause();
        if (causeThrowable == null) {
            return e;
        }
        while (causeThrowable.getCause() != null) {
            causeThrowable = causeThrowable.getCause();
        }
        return causeThrowable;
    }

}
