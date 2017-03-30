package com.domain.common.web.controller;

import com.domain.common.utils.XssFilterUtil;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class XssFilterMultipartHttpServletRequest extends
        DefaultMultipartHttpServletRequest {

    public XssFilterMultipartHttpServletRequest(HttpServletRequest request,
                                                MultiValueMap<String, MultipartFile> mpFiles,
                                                Map<String, String[]> mpParams,
                                                Map<String, String> mpParamContentTypes) {

        super(request, mpFiles, mpParams, mpParamContentTypes);
    }

    public XssFilterMultipartHttpServletRequest(HttpServletRequest request) {

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
