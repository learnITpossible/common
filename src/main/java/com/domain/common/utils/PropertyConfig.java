package com.domain.common.utils;

import java.io.UnsupportedEncodingException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public class PropertyConfig {

    private static ResourceBundle rb = null;

    private static Logger logger = Logger.getLogger(PropertyConfig.class);

    static {
        try {
            if (null == rb) {
                rb = ResourceBundle.getBundle("property_config");
            }
        } catch (Exception e) {

            logger.info("读取属性文件-失败！-  原因：property_config.properties文件路径错误或者文件不存在");
            logger.error(e);
        }
    }

    /**
     * 根据键获取对应的值
     * @param key
     * @return
     */
    public static String getPropertyValue(String key) {

        String value = rb.getString(key);
        try {
            value = new String(value.getBytes("ISO8859-1"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }
        return value;
    }

    /**
     * 判断是否存在key
     */
    public static boolean containsKey(String key) {

        return rb.containsKey(key);
    }
}
