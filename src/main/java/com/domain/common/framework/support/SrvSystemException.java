package com.domain.common.framework.support;

/**
 * 针对Srv服务调用时发生的系统错误
 * @author xuejiao
 */
public class SrvSystemException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String stackInfo;

    private String msg;

    public SrvSystemException(String stackInfo, String msg) {

        super();
        this.stackInfo = stackInfo;
        this.msg = msg;
    }

    public String getStackInfo() {

        return stackInfo;
    }

    public void setStackInfo(String stackInfo) {

        this.stackInfo = stackInfo;
    }

    public String getMsg() {

        return msg;
    }

    public void setMsg(String msg) {

        this.msg = msg;
    }

    public SrvSystemException() {

    }

    /**
     * @param message
     */
    public SrvSystemException(String message) {

        super(message);
    }

    /**
     * @param cause
     */
    public SrvSystemException(Throwable cause) {

        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public SrvSystemException(String message, Throwable cause) {

        super(message, cause);
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public SrvSystemException(String message, Throwable cause,
                              boolean enableSuppression, boolean writableStackTrace) {

        super(message, cause, enableSuppression, writableStackTrace);
    }

}
