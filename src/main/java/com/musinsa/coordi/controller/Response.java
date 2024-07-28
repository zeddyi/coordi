package com.musinsa.coordi.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response {
    private boolean success;
    private String code;
    private String message;

    public Response(boolean success, String code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }
}
