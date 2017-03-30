package com.domain.common.utils;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证格式类
 */
public class ValidateUtil {

    public final static String IS_MOBILE_NO = "^((13[0-9])|(14[0-9])|(17[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";

    public final static String IS_TEL_NO = "\\d{4}-\\d{8}|\\d{4}-\\d{7}|\\d(3)-\\d(8)";

    public final static String IS_FLOAT = "^(\\d+(\\.\\d+)?|0*(\\.\\d+)?)$";

    public final static String IS_INTEGER = "^\\d+(\\.0*)?$";

    public final static String IS_YYYYMMDDHHMMSS = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-9]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";

    public final static String IS_CAR_NUMBER = "^[\u4e00-\u9fa5]{1}[a-zA-Z]{1}[a-zA-Z0-9]{5,6}$";

    /**
     * 判断所给参数是否为手机号码
     * @param mobiles 数据字符串
     * @return true，是手机号码; false,不是手机号码
     */
    public static boolean isMobileNO(String mobiles) {

        Pattern p = Pattern.compile(IS_MOBILE_NO);

        Matcher m = p.matcher(mobiles);

        return m.matches();

    }

    /**
     * 检查电话输入 是否正确 正确格 式 012-87654321、0123-87654321、0123－7654321
     * @param value
     * @return
     */
    public static boolean checkTel(String value) {

        return value.matches("\\d{4}-\\d{8}|\\d{4}-\\d{7}|\\d(3)-\\d(8)");
    }

    /**
     * 判断所给对象是否为浮点数
     * @param value String 数据
     * @return boolean
     */
    public static boolean isFloat(String value) {

        Pattern pattern = Pattern.compile("^(\\d+(\\.\\d+)?|0*(\\.\\d+)?)$");
        Matcher matcher = pattern.matcher(StringUtils.trim(value));
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    /**
     * 判断所给对象是否为整数
     * @param value String 数据
     * @return boolean
     */
    public static boolean isInteger(String value) {

        Pattern pattern = Pattern.compile("^\\d+(\\.0*)?$");
        Matcher matcher = pattern.matcher(StringUtils.trim(value));
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    /**
     * 判断所给对象是否是日期
     * @param strDate 日期数据
     * @return boolean
     */
    public static boolean isDateFor24Format(String strDate) {

        String eL = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-9]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
        Pattern p = Pattern.compile(eL);
        Matcher m = p.matcher(strDate);
        if (m.matches()) {
            return true;
        }
        return false;
    }

    /**
     * 检查输入是否 超出规定长度
     * @param length
     * @param value
     * @return
     */
    public static boolean checkLength(String value, int length) {

        return ((value == null || "".equals(value.trim())) ? 0 : value.length()) <= length;
    }

    /**
     * @param value
     * @param validateType
     * @return
     */
    public static boolean validate(String value, String validateType) {

        Pattern p = Pattern.compile(validateType);
        Matcher m = p.matcher(value);
        if (m.matches()) {
            return true;
        }
        return false;
    }
}