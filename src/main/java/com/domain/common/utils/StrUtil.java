package com.domain.common.utils;

public class StrUtil {

    /**
     * 按字节长度截字符串
     * @param str
     * @param toCount
     * @param more
     * @return
     */
    public static String substring(String str, int toCount, String more)

    {

        int reInt = 0;

        StringBuffer reStr = new StringBuffer();

        if (str == null)

            return "";

        char[] tempChar = str.toCharArray();

        for (int kk = 0; (kk < tempChar.length); kk++) {

            String s1 = String.valueOf(tempChar[kk]);

            byte[] b = s1.getBytes();

            reInt += b.length;
            if (reInt > toCount) {
                break;
            }

            reStr.append(tempChar[kk]);

        }

        if (toCount == reInt || (toCount == reInt - 1))
            reStr.append(more);

        return reStr.toString();

    }

}
