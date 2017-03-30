package com.domain.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

/**
 * JsonHelper 会过滤private, protected 类型的字段, 此类为解决此问题
 * @author yejianzhong
 */
public class JsonHelper2 {

    private static Gson gson = new GsonBuilder().create();

    private JsonHelper2() {

    }

    /***************object to json*****************/

    public static String toJson(Object obj) {

        return gson.toJson(obj);
    }

    public static <T> T fromJson(String s, Class clasz) {

        try {
            return (T) gson.fromJson(s, clasz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> fromJson(String s, java.lang.reflect.Type type) {

        try {
            return (List<T>) gson.fromJson(s, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
