package com.tmyx.backend.common.exception;

public class CustomException extends RuntimeException{
    private Integer code;

    public CustomException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
