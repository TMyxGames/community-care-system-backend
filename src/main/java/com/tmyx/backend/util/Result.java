package com.tmyx.backend.util;


import lombok.Data;

@Data
public class Result<T> {
    private Integer code; // 状态码
    private String msg; // 返回信息
    private T data; // 返回数据

    // 默认成功
    public static <T> Result<T> success(T data) {
        return build(200, "操作成功", data);
    }

    // 默认失败
    public static <T> Result<T> error(String msg) {
        return build(500, msg, null);
    }

    // 自定义状态码
    public static <T> Result<T> build(Integer code, String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    // 不带数据的成功
    public static <T> Result<T> success() {
        return build(200, "操作成功", null);
    }

    // 401, 403...
    public static <T> Result<T> error(Integer code, String msg) {
        return build(code, msg, null);
    }



    // getter, setter
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }


}
