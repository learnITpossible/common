package com.domain.common.utils;

import org.apache.commons.beanutils.PropertyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CollectionUtil {

    /**
     * 将一个src列表指定的几个字段，合并到dest列表，通过destId==srcId关联
     * @param destList   目标列表
     * @param srcList    源列表
     * @param destFields 目标列表要设置的字段名
     * @param srcFields  从源列表要取值字段，必须与destFields顺序长度一致
     * @param destId     关联id
     * @param srcId      关联id
     * @throws Exception
     */
    public static void mergeList(List<? extends Object> destList
            , List<? extends Object> srcList, String[] destFields,
                                 String[] srcFields, String destId, String srcId) throws Exception {

        for (Object destObj : destList) {
            Object srcObj = null;
            Object destIdValue = getObjectFieldValue(destObj, destId);
            for (Object srcObj2 : srcList) {
                Object srcIdValue = getObjectFieldValue(srcObj2, srcId);
                if (destIdValue.equals(srcIdValue)) {
                    srcObj = srcObj2;
                    break;
                }
            }
            if (srcObj == null) {
                continue;
            }
            for (int i = 0; i < destFields.length; i++) {
                Object srcFieldValue = getObjectFieldValue(srcObj, srcFields[i]);
                setObjectFieldValue(destObj, destFields[i], srcFieldValue);
            }
        }
    }

    /**
     * 获取将列表的指定字段值存放到独立的list
     * @param list  源列表
     * @param field 要获取的字段
     * @return
     * @throws Exception
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List getListFieldValues(List<? extends Object> list, String field) throws Exception {

        List ids = new ArrayList();
        for (Object destObj : list) {
            Object destIdValue = getObjectFieldValue(destObj, field);
            ids.add(ids.size(), destIdValue);
        }
        return ids;
    }

    @SuppressWarnings("rawtypes")
    public static Object getObjectFieldValue(Object obj, String field) throws Exception {

        if (obj instanceof Map) {
            return ((Map) obj).get(field);
        } else {
            return PropertyUtils.getProperty(obj, field);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void setObjectFieldValue(Object obj, String field, Object value) throws Exception {

        if (obj instanceof Map) {
            ((Map) obj).put(field, value);
        } else {
            PropertyUtils.setProperty(obj, field, value);
        }
    }

}
