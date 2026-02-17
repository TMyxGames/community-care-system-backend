package com.tmyx.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketResult<T> {
    private String type; // 消息类型
    private T data; // 业务数据

    public static <T> WebSocketResult<T> build(String type, T data) {
        return new WebSocketResult<>(type, data);
    }
}
