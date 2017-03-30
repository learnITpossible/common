package com.domain.common.utils;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author Frank
 */
public class F {

    public static int FloatCmp(double a, double b) {

        double f = a - b;
        if (f < -0.0001)
            return -1;
        if (f > 0.0001)
            return 1;
        return 0;
    }

    public static BigDecimal S2Decimal(String s) {

        return F2Decimal(Double.parseDouble(s));
    }

    public static BigDecimal F2Decimal(double a) {

        return new BigDecimal(String.format("%.2f", a));
    }

    public static BigDecimal F2Decimal5(double a) {

        return new BigDecimal(String.format("%.5f", a));
    }

    public static BigDecimal RmbMulti(BigDecimal a, BigDecimal b) {

        BigDecimal c = a.multiply(b);
        return c.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public static boolean isTimeBetween(Timestamp start1, Date start, Date end) {

        return start1.after(start) && start1.before(end);
    }

    public static String RmbToStr(BigDecimal d) {

        return d.toPlainString();
    }

    public static int[] int2Bytes(int value) {

        String binaryStr = Integer.toBinaryString(value);
        int[] ret = new int[binaryStr.length()];
        for (int i = 0; i < binaryStr.length(); i++)
            ret[ret.length - i - 1] = binaryStr.charAt(i) == '1' ? 1 : 0;
        return ret;
    }

    public static Set<Integer> string2sets(String s) {

        Set<Integer> result = new HashSet<Integer>();
        String[] ss = s.split(",");
        for (int i = 0; i < ss.length; i++) {
            result.add(Integer.parseInt(ss[i]));
        }
        return result;
    }

    public static List<Integer> string2lists(String s) {

        List<Integer> result = new ArrayList<Integer>();
        if (StringUtils.isBlank(s)) {
            return result;
        }
        String[] ss = s.split(",");
        for (int i = 0; i < ss.length; i++) {
            result.add(Integer.parseInt(ss[i]));
        }
        return result;
    }

    public static Map<String, String[]> parseQueryString(String s) {

        String valArray[] = null;
        if (s == null) {
            throw new IllegalArgumentException();
        }
        Map params = new HashMap<String, String[]>();
        StringBuffer sb = new StringBuffer();
        StringTokenizer st = new StringTokenizer(s, "&");
        while (st.hasMoreTokens()) {
            String pair = (String) st.nextToken();
            int pos = pair.indexOf('=');
            if (pos == -1) {
                throw new IllegalArgumentException();
            }
            String key = parseName(pair.substring(0, pos), sb);
            String val = parseName(pair.substring(pos + 1, pair.length()), sb);
            if (params.containsKey(key)) {
                String oldVals[] = (String[]) params.get(key);
                valArray = new String[oldVals.length + 1];
                for (int i = 0; i < oldVals.length; i++)
                    valArray[i] = oldVals[i];
                valArray[oldVals.length] = val;
            } else {
                valArray = new String[1];
                valArray[0] = val;
            }
            params.put(key, valArray);
        }
        return params;
    }

    private static String parseName(String s, StringBuffer sb) {

        sb.setLength(0);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '+':
                    sb.append(' ');
                    break;
                case '%':
                    try {
                        sb.append((char) Integer.parseInt(s.substring(i + 1, i + 3), 16));
                        i += 2;
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException();
                    } catch (StringIndexOutOfBoundsException e) {
                        String rest = s.substring(i);
                        sb.append(rest);
                        if (rest.length() == 2)
                            i++;
                    }

                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    public static void wait(int second) {

        try {
            Thread.sleep(second * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据坐标获取距离
     * @param lng1
     * @param lat1
     * @param lng2
     * @param lat2
     * @return 米
     */
    public static double getDistance(double lng1, double lat1, double lng2, double lat2) {

        double EARTH_RADIUS = 6371.393; // 地球的半径千米
        double radLat1 = lat1 * Math.PI / 180.0;
        double radLat2 = lat2 * Math.PI / 180.0;
        double a = lat1 * Math.PI / 180.0 - lat2 * Math.PI / 180.0;
        double b = lng1 * Math.PI / 180.0 - lng2 * Math.PI / 180.0;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS * 1000;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

}
