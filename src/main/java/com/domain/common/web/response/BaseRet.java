package com.domain.common.web.response;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class BaseRet implements Serializable {

    private static final long serialVersionUID = 71003516985493598L;

    public static final BaseRet SUCC = new BaseRet(0);

    public static final BaseRet EXCEPTION_ERROR = new BaseRet(998, "发生异常!");

    public static final BaseRet UNKOWN_ERROR = new BaseRet(999, "未知错误!");

    public static final BaseRet ERROR_VALIDATOR_FIELDS = new BaseRet(996, "参数验证错误!");

    public static final BaseRet NULL_SIG = new BaseRet(100, "Invalid signature,is null!");

    public static final BaseRet INVALID_SIG = new BaseRet(100, "Invalid signature");

    public static final BaseRet INVALID_TOKEN = new BaseRet(101, "Invalid token or appkey!");

    public static final BaseRet NULL_TOKEN = new BaseRet(101, "Invalid token or appkey,is null!");

    public int code;

    public String msg;

    public BaseRet() {

    }

    public BaseRet(int code) {

        this.code = code;
        this.msg = "";
    }

    public BaseRet(int code, String msg) {

        this.code = code;
        this.msg = msg;
    }

    public BaseRet(int code, String msg, Object... objects) {

        this.code = code;
        this.msg = String.format(msg, objects);
    }

    public int getCode() {

        return code;
    }

    public void setCode(int code) {

        this.code = code;
    }

    public String getMsg() {

        return msg;
    }

    public void setMsg(String msg) {

        this.msg = msg;
    }

    public static BaseRet paramlizeRet(BaseRet ret, Object... args) {

        BaseRet ret2 = new BaseRet(ret.code, ret.msg, args);
        return ret2;
    }

}
