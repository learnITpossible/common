package com.domain.common.web.controller.validators;

import java.io.Serializable;

public class ControllerValidatorError implements Serializable {

    private static final long serialVersionUID = 3171885958010701119L;

    String name;

    String msg;

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getMsg() {

        return msg;
    }

    public void setMsg(String msg) {

        this.msg = msg;
    }

}
