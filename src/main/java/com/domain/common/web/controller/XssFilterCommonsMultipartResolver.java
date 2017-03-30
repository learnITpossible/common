package com.domain.common.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

public class XssFilterCommonsMultipartResolver extends CommonsMultipartResolver {

    @Override
    public MultipartHttpServletRequest resolveMultipart(
            HttpServletRequest request) throws MultipartException {

        MultipartParsingResult parsingResult = parseRequest(request);

        return new XssFilterMultipartHttpServletRequest(request, parsingResult.getMultipartFiles(),
                parsingResult.getMultipartParameters(), parsingResult.getMultipartParameterContentTypes());

    }

}
