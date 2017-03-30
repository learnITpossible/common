package com.domain.common.utils;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class WebUtil {

    /**
     * 取得客户端请求 真实IP
     */
    public static String getRealIp(HttpServletRequest request) {

        String ip = request.getHeader("x-forwarded-for");
        if (ip != null && ip.contains(",")) {
            ip = ip.substring(0, ip.indexOf(","));
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是本地测试或以上步骤没有取得ip没从请求参数中获取IP
        if (("127.0.0.1".equals(ip) || StringUtils.isEmpty(ip))
                && !StringUtils.isEmpty(request.getParameter("ip")))
            ip = request.getParameter("ip");
        return ip;
    }
}
