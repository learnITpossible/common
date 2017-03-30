package com.domain.common.web.response;

import com.domain.common.web.controller.validators.ControllerValidatorError;

import java.util.List;

public class ResponseUtils {

    public static Response succ() {

        return new Response(BaseRet.SUCC, "");
    }

    public static Response succ(Object obj) {

        return new Response(BaseRet.SUCC, obj);
    }

    public static Response instance(BaseRet ret) {

        return new Response(ret);
    }

    public static Response instance(int status, Object obj) {

        return new Response(new BaseRet(status), obj);
    }

    public static Response instance(int status, String msg) {

        return new Response(new BaseRet(status, msg));
    }

    public static Response instance(int status, String msg, Object obj) {

        return new Response(new BaseRet(status, msg), obj);
    }

    public static Response instance(int status, String msg, Object obj, List<ControllerValidatorError> errors) {

        return new Response(new BaseRet(status, msg), obj, errors);
    }

}
