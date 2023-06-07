package com.kirie.common.vo;

public record Result<T>(Integer code, String message, T data) {
    // success
    public static <T> Result<T> success() {
        return new Result<>(20000, "success", null);
    }
    public static <T> Result<T> success(T data) {
        return new Result<>(20000, "success", data);
    }
    public static <T> Result<T> success(T data, String message) {
        return new Result<>(20000, message, data);
    }
    public static <T> Result<T> success(String message) {
        return new Result<>(20000, message, null);
    }

    // error
    public static <T> Result<T> fail() {
        return new Result<>(20001, "fail", null);
    }
    public static <T> Result<T> fail(Integer code) {
        return new Result<>(code, "fail", null);
    }
    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }
    public static <T> Result<T> fail(String message) {
        return new Result<>(20001, message, null);
    }
}
