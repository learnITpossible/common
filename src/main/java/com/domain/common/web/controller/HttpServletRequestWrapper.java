package com.domain.common.web.controller;

import com.domain.common.utils.F;
import org.apache.commons.lang.StringUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

/**
 * 重载request方法，主要用在RSA加解密的方法中
 * @author Frank
 */
public class HttpServletRequestWrapper implements HttpServletRequest {

    private HttpServletRequest request;

    private Map<String, String[]> params = null;

    public static HttpServletRequest wrap(HttpServletRequest req, String queryString) {

        return new HttpServletRequestWrapper(req, F.parseQueryString(queryString));
    }

    public static HttpServletRequest wrap(HttpServletRequest req, String replaceParamName, String[] replaceParamValue) {

        Map<String, String[]> params = req.getParameterMap();
        Map<String, String[]> map = new HashMap<String, String[]>();
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            map.put(param.getKey(), param.getValue());
        }
        map.put(replaceParamName, replaceParamValue);
        return new HttpServletRequestWrapper(req, map);
    }

    private HttpServletRequestWrapper(HttpServletRequest req, Map<String, String[]> params) {

        this.request = req;
        this.params = params;
    }

    ///////////////////Begin.重载getParameter方法/////////////////
    @Override
    public String getParameter(String name) {

        if (null != params) {
            String[] vals = params.get(name);
            return null != vals ? StringUtils.join(vals, ",") : null;
        } else {
            return request.getParameter(name);
        }
    }

    @Override
    public Enumeration<String> getParameterNames() {

        if (null != params) {
            return Collections.enumeration(params.keySet());
        } else {
            return request.getParameterNames();
        }
    }

    @Override
    public String[] getParameterValues(String name) {

        if (null != params) {
            return params.get(name);
        } else {
            return request.getParameterValues(name);
        }
    }

    @Override
    public Map<String, String[]> getParameterMap() {

        if (null != params) {
            return params;
        } else {
            return request.getParameterMap();
        }
    }
    ///////////////////End.重载getParameter方法/////////////////

    @Override
    public Object getAttribute(String name) {

        return request.getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {

        return request.getAttributeNames();
    }

    @Override
    public String getCharacterEncoding() {

        return request.getCharacterEncoding();
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

        request.setCharacterEncoding(env);
    }

    @Override
    public int getContentLength() {

        return request.getContentLength();
    }

    @Override
    public String getContentType() {

        return request.getContentType();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        return request.getInputStream();
    }

    @Override
    public String getProtocol() {

        return request.getProtocol();
    }

    @Override
    public String getScheme() {

        return request.getScheme();
    }

    @Override
    public String getServerName() {

        return request.getServerName();
    }

    @Override
    public int getServerPort() {

        return request.getServerPort();
    }

    @Override
    public BufferedReader getReader() throws IOException {

        return request.getReader();
    }

    @Override
    public String getRemoteAddr() {

        return request.getRemoteAddr();
    }

    @Override
    public String getRemoteHost() {

        return request.getRemoteAddr();
    }

    @Override
    public void setAttribute(String name, Object o) {

        request.setAttribute(name, o);
    }

    @Override
    public void removeAttribute(String name) {

        request.removeAttribute(name);
    }

    @Override
    public Locale getLocale() {

        return request.getLocale();
    }

    @Override
    public Enumeration<Locale> getLocales() {

        return request.getLocales();
    }

    @Override
    public boolean isSecure() {

        return request.isSecure();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {

        return request.getRequestDispatcher(path);
    }

    @Override
    public String getRealPath(String path) {

        return request.getRealPath(path);
    }

    @Override
    public int getRemotePort() {

        return request.getRemotePort();
    }

    @Override
    public String getLocalName() {

        return request.getLocalName();
    }

    @Override
    public String getLocalAddr() {

        return request.getLocalAddr();
    }

    @Override
    public int getLocalPort() {

        return request.getLocalPort();
    }

    @Override
    public ServletContext getServletContext() {

        return request.getServletContext();
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {

        return request.startAsync();
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {

        return request.startAsync(servletRequest, servletResponse);
    }

    @Override
    public boolean isAsyncStarted() {

        return request.isAsyncStarted();
    }

    @Override
    public boolean isAsyncSupported() {

        return request.isAsyncSupported();
    }

    @Override
    public AsyncContext getAsyncContext() {

        return request.getAsyncContext();
    }

    @Override
    public DispatcherType getDispatcherType() {

        return request.getDispatcherType();
    }

    @Override
    public String getAuthType() {

        return request.getAuthType();
    }

    @Override
    public Cookie[] getCookies() {

        return request.getCookies();
    }

    @Override
    public long getDateHeader(String name) {

        return request.getDateHeader(name);
    }

    @Override
    public String getHeader(String name) {

        return request.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {

        return request.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {

        return request.getHeaderNames();
    }

    @Override
    public int getIntHeader(String name) {

        return request.getIntHeader(name);
    }

    @Override
    public String getMethod() {

        return request.getMethod();
    }

    @Override
    public String getPathInfo() {

        return request.getPathInfo();
    }

    @Override
    public String getPathTranslated() {

        return request.getPathTranslated();
    }

    @Override
    public String getContextPath() {

        return request.getContextPath();
    }

    @Override
    public String getQueryString() {

        return request.getQueryString();
    }

    @Override
    public String getRemoteUser() {

        return request.getRemoteUser();
    }

    @Override
    public boolean isUserInRole(String role) {

        return request.isUserInRole(role);
    }

    @Override
    public Principal getUserPrincipal() {

        return request.getUserPrincipal();
    }

    @Override
    public String getRequestedSessionId() {

        return request.getRequestedSessionId();
    }

    @Override
    public String getRequestURI() {

        return request.getRequestURI();
    }

    @Override
    public StringBuffer getRequestURL() {

        return request.getRequestURL();
    }

    @Override
    public String getServletPath() {

        return request.getServletPath();
    }

    @Override
    public HttpSession getSession(boolean create) {

        return request.getSession(create);
    }

    @Override
    public HttpSession getSession() {

        return request.getSession();
    }

    @Override
    public boolean isRequestedSessionIdValid() {

        return request.isRequestedSessionIdValid();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {

        return request.isRequestedSessionIdFromCookie();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {

        return request.isRequestedSessionIdFromURL();
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {

        return request.isRequestedSessionIdFromUrl();
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {

        return request.authenticate(response);
    }

    @Override
    public void login(String username, String password) throws ServletException {

        request.login(username, password);
    }

    @Override
    public void logout() throws ServletException {

        request.logout();
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {

        return request.getParts();
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {

        return request.getPart(name);
    }
}
