package com.tmyx.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationMessage {
    private String type;
    private Object data;
}
