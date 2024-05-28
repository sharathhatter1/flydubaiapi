package com.fd.rest.api;

public enum StatusCode {
    CODE_200(200, "");

    public final int code;
    public final String msg;

    StatusCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
