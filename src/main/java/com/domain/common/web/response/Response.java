package com.domain.common.web.response;

import com.domain.common.web.controller.validators.ControllerValidatorError;
import com.domain.common.utils.JsonHelper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Response {

    public final static String SUCCESS;

    public final static String FAILURE;

    static {
        SUCCESS = JsonHelper.toJson(new Response(BaseRet.SUCC));
        FAILURE = JsonHelper.toJson(new Response(BaseRet.UNKOWN_ERROR));
    }

    public BaseRet ret;

    public Object data;

    public List<ControllerValidatorError> fieldErrors;

    public Response() {

    }

    public Response(BaseRet ret) {

        this.ret = ret;
        this.data = "";
    }

    public Response(BaseRet ret, Object data) {

        this.ret = ret;
        this.data = data;
    }

    public Response(BaseRet ret, Object data, List<ControllerValidatorError> errors) {

        this.ret = ret;
        this.data = data;
        this.fieldErrors = errors;
    }
}
