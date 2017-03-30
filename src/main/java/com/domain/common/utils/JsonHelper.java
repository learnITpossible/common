package com.domain.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

public class JsonHelper {

    public static final Type TYPE_LIST_STRING = new TypeToken<List<String>>() {
    }.getType();

    public static final Type TYPE_LIST_INTEGER = new TypeToken<List<Integer>>() {
    }.getType();

    public static final Type TYPE_LIST_LONG = new TypeToken<List<Long>>() {
    }.getType();

    public static final Type TYPE_LIST_FLOAT = new TypeToken<List<Float>>() {
    }.getType();

    public static final Type TYPE_SET_STRING = new TypeToken<Set<String>>() {
    }.getType();

    public static final Type TYPE_SET_INTEGER = new TypeToken<Set<Integer>>() {
    }.getType();

    public static final Type TYPE_SET_LONG = new TypeToken<Set<Long>>() {
    }.getType();

    public static final Type TYPE_SET_FLOAT = new TypeToken<Set<Float>>() {
    }.getType();

    private static Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.PROTECTED).create();

    private JsonHelper() {

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

    public static <T> List<T> fromJson(String s, Type type) {

        try {
            return (List<T>) gson.fromJson(s, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> Set<T> fromJsonSet(String s, Type type) {

        try {
            return (Set<T>) gson.fromJson(s, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
