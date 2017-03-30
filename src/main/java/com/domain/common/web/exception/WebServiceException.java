package com.domain.common.web.exception;

import com.domain.common.web.response.BaseRet;

public class WebServiceException extends Exception {

    private static final long serialVersionUID = -7074311181736387942L;

    BaseRet ret;

    public WebServiceException(BaseRet ret) {

        super();
        this.ret = ret;
    }

    public WebServiceException(String message, Throwable cause,
                               boolean enableSuppression, boolean writableStackTrace, BaseRet ret) {

        super(message, cause, enableSuppression, writableStackTrace);
        this.ret = ret;
    }

    public WebServiceException(String message, Throwable cause, BaseRet ret) {

        super(message, cause);
        this.ret = ret;
    }

    public WebServiceException(String message, BaseRet ret) {

        super(message);
        this.ret = ret;
    }

    public WebServiceException(Throwable cause, BaseRet ret) {

        super(cause);
        this.ret = ret;
    }

    public BaseRet getRet() {

        return ret;
    }

    public void setRet(BaseRet ret) {

        this.ret = ret;
    }

    @Override
    public String getMessage() {

        if (ret == null) return null;
        return ret.getCode() + ": " + ret.getMsg();
    }

}
