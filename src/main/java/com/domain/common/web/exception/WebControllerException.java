package com.domain.common.web.exception;

import com.domain.common.web.response.BaseRet;

public class WebControllerException extends WebServiceException {

    private static final long serialVersionUID = -6340860422045594843L;

    public WebControllerException(BaseRet ret) {

        super(ret);
        // TODO Auto-generated constructor stub
    }

    public WebControllerException(String message, BaseRet ret) {

        super(message, ret);
        // TODO Auto-generated constructor stub
    }

    public WebControllerException(String message, Throwable cause,
                                  boolean enableSuppression, boolean writableStackTrace, BaseRet ret) {

        super(message, cause, enableSuppression, writableStackTrace, ret);
        // TODO Auto-generated constructor stub
    }

    public WebControllerException(String message, Throwable cause, BaseRet ret) {

        super(message, cause, ret);
        // TODO Auto-generated constructor stub
    }

    public WebControllerException(Throwable cause, BaseRet ret) {

        super(cause, ret);
        // TODO Auto-generated constructor stub
    }

}
