package com.domain.common.web.controller;

import com.domain.common.utils.XssFilterUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Map;

public class XssFilterRequestWrapper extends HttpServletRequestWrapper {

    public XssFilterRequestWrapper(HttpServletRequest request) {

        super(request);
    }

    @Override
    public String getParameter(String name) {

        return XssFilterUtil.filter2Text(super.getParameter(name));
    }

    @Override
    public String[] getParameterValues(String name) {

        String[] values = super.getParameterValues(name);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                values[i] = XssFilterUtil.filter2Text(values[i]);
            }
        }
        return values;
    }

    public Map<String, String[]> getParameterMap() {

        Map<String, String[]> parameterMap = super.getParameterMap();
        if (parameterMap != null) {
            for (String key : parameterMap.keySet()) {
                String[] values = parameterMap.get(key);
                if (values == null) {
                    continue;
                }
                for (int i = 0; i < values.length; i++) {
                    values[i] = XssFilterUtil.filter2Text(values[i]);
                }
            }
        }
        return parameterMap;
    }

}
