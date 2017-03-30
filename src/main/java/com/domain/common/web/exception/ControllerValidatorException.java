package com.domain.common.web.exception;

import com.domain.common.web.controller.validators.ControllerValidatorError;

import java.util.ArrayList;
import java.util.List;

public class ControllerValidatorException extends RuntimeException {

    private static final long serialVersionUID = -5264276166001545157L;

    List<ControllerValidatorError> errors = new ArrayList<ControllerValidatorError>();

    public void addError(ControllerValidatorError error) {

        errors.add(errors.size(), error);
    }

    public List<ControllerValidatorError> getErrors() {

        return errors;
    }

    public ControllerValidatorException() {

        super();
    }

    public ControllerValidatorException(String message, Throwable cause,
                                        boolean enableSuppression, boolean writableStackTrace) {

        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    public ControllerValidatorException(String message, Throwable cause) {

        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public ControllerValidatorException(String message) {

        super(message);
        // TODO Auto-generated constructor stub
    }

    public ControllerValidatorException(Throwable cause) {

        super(cause);
        // TODO Auto-generated constructor stub
    }

}
