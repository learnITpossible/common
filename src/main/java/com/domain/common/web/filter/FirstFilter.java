package com.domain.common.web.filter;

import com.domain.common.web.controller.XssFilterRequestWrapper;
import com.domain.common.utils.ThreadUtil;
import com.domain.common.web.controller.HttpServletResponseCopier;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class FirstFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(FirstFilter.class);

    private boolean isHttpsForce;

    private int httpPort;

    private int httpsPort;

    private Set<String> notLogParamsUrls = new HashSet<>();

    private Set<String> notBrowserParamsUrls = new HashSet<>();

    private Set<String> notCSPUrls = new HashSet<>();

    private Set<String> notHttpsUrls = new HashSet<>();

    private String allowOriginDomainStr;

    private Set<String> allowOriginDomain = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        WebApplicationContext context = null;
        while (context == null) {
            context = (WebApplicationContext) filterConfig.getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
            if (context == null) {
                ThreadUtil.sleep(500);
            }
        }

        if (StringUtils.isNotBlank(allowOriginDomainStr)) {
            String[] urlArray = org.springframework.util.StringUtils.tokenizeToStringArray(allowOriginDomainStr, ",;\r\n\t");
            if (urlArray != null && urlArray.length > 0) {
                Collections.addAll(this.allowOriginDomain, urlArray);
            }
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = new XssFilterRequestWrapper((HttpServletRequest) servletRequest);
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        logger.debug("url = " + request.getRequestURL());
        String servletPath = request.getServletPath();
        try {
            String referer = request.getHeader("referer");
            logger.debug("referer = " + referer);
            if (StringUtils.isNotBlank(referer)) {
                URL url = new URL(referer);
                String domain = url.getProtocol() + "://" + url.getHost();
                if (url.getPort() != -1) {
                    domain += ":" + url.getPort();
                }
                logger.debug("domain = " + domain);
                if (CollectionUtils.isNotEmpty(allowOriginDomain) && allowOriginDomain.contains(domain)) {
                    // 允许跨域
                    response.setHeader("Access-Control-Allow-Origin", domain);
                    response.setHeader("Access-Control-Allow-Credentials", "true");
                    response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
                    response.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers,Authorization, X-Requested-With, Last-Modified, If-Modified-Since");
                }
            }
            // 记log
            try {
                HttpServletResponseCopier respCopier = new HttpServletResponseCopier(response);
                chain.doFilter(request, respCopier);
                respCopier.flushBuffer();
            } catch (Exception e) {
                try {
                    logger.error(String.format("%s doFilter get Error:%s", servletPath, e.getMessage()), e);
                    // 写死，临时方案
                    response.getWriter().println("{\"code\":999,\"msg\":\"Filter Unknown Error!\",\"data\":\"\"}");
                } catch (Exception e2) {
                    logger.error(String.format("%s doFilter do error log get Error:%s", servletPath, e2.getMessage()), e2);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static String getUri(ServletRequest req) {

        try {
            Map<String, String[]> parameterMap = null;
            if (req != null) {
                parameterMap = req.getParameterMap();
            }
            if (parameterMap != null && parameterMap.size() > 0) {
                StringBuilder builder = new StringBuilder();
                Iterator<Map.Entry<String, String[]>> iterator = parameterMap.entrySet().iterator();
                if (iterator.hasNext()) {
                    builder.append(buildParameter(iterator.next()));
                }
                while (iterator.hasNext()) {
                    builder.append("&").append(buildParameter(iterator.next()));
                }
                return builder.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String buildParameter(Map.Entry<String, String[]> entry) {

        try {
            if (entry != null) {
                StringBuilder builder = new StringBuilder();
                builder.append(entry.getKey()).append("=");
                String[] values = entry.getValue();
                if (values != null && values.length > 0) {
                    if (values.length == 1) {
                        builder.append(values[0]);
                    } else {
                        builder.append(StringUtils.join(values, ","));
                    }
                }
                return builder.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void destroy() {

    }

    /**
     * 判断是否为不需要进行https 跳转的url
     * @return true: skip filter, false: do filter
     */
    private boolean isNotHttpsUrls(String servletPath) {

        for (String url : notHttpsUrls) {
            if (servletPath.startsWith(url)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从request中获取域名(正服)、IP(测试环境)
     */
    private String getHost(HttpServletRequest request) {

        String url = request.getRequestURL().toString();
        url = url.substring(url.indexOf("//") + 2);
        if (url.contains("/")) {
            url = url.substring(0, url.indexOf("/"));
        }
        if (url.contains(":")) {
            url = url.substring(0, url.indexOf(":"));
        }
        return url;

    }

    /**
     * 请求重定向：notHttpsUrls 强制重定向到http,其他的请求强制重定向到https
     */
    private String processRedirect(HttpServletRequest httpRequest) {

        String url;

        String isHttps = httpRequest.getHeader("Is-Https");
        String protocol = "http";
        if (StringUtils.isNotBlank(isHttps)) {
            if ("true".equals(isHttps.trim())) {
                protocol = "https";
            }
        }

        if (isNotHttpsUrls(httpRequest.getServletPath().trim())) {
            if ("http".equals(protocol)) {
                return null;
            }
            url = "http://" + getHost(httpRequest);
            if (httpPort != 80) {
                url += ":" + httpPort;
            }
        } else {
            if ("https".equals(protocol)) {
                return null;
            }
            url = "https://" + getHost(httpRequest);
            if (httpsPort != 443) {
                url += ":" + httpsPort;
            }
        }

        if (StringUtils.isNotBlank(httpRequest.getRequestURI())) {
            String lastUrl = httpRequest.getRequestURI();
            String qs = httpRequest.getQueryString();
            if (StringUtils.isNotBlank(qs)) {
                lastUrl = lastUrl + "?" + qs;
            }
            url = url + lastUrl;
        }
        return url;
    }

    public void setIsHttpsForce(String isHttpsForce) {

        this.isHttpsForce = isHttpsForce.equals("true");
    }

    public void setHttpsForce(boolean httpsForce) {

        isHttpsForce = httpsForce;
    }

    public void setHttpPort(int httpPort) {

        this.httpPort = httpPort;
    }

    public void setHttpsPort(int httpsPort) {

        this.httpsPort = httpsPort;
    }

    public void setNotLogParamsUrls(Set<String> notLogParamsUrls) {

        this.notLogParamsUrls = notLogParamsUrls;
    }

    public void setNotBrowserParamsUrls(Set<String> notBrowserParamsUrls) {

        this.notBrowserParamsUrls = notBrowserParamsUrls;
    }

    public void setNotCSPUrls(Set<String> notCSPUrls) {

        this.notCSPUrls = notCSPUrls;
    }

    public void setNotHttpsUrls(Set<String> notHttpsUrls) {

        this.notHttpsUrls = notHttpsUrls;
    }

    public void setAllowOriginDomainStr(String allowOriginDomainStr) {

        this.allowOriginDomainStr = allowOriginDomainStr;
    }
}
