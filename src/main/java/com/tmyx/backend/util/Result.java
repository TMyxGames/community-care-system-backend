package com.tmyx.backend.util;


import lombok.Data;

@Data
public class Result<T> {
    private Integer code; // 状态码
    private String msg; // 返回信息
    private T data; // 返回数据

    // 带数据的成功方法
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    // 不带数据的成功方法
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        return result;
    }

    // 带数据的错误方法
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }
}
