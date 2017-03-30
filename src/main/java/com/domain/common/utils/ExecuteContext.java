package com.domain.common.utils;

public class ExecuteContext {

    public static ThreadLocal<String> httpByBusiness = new ThreadLocal<String>();

    public static ThreadLocal<String> flag = new ThreadLocal<String>();
}
