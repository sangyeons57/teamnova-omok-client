package com.example.application.port.in;

public class UResults {
    public static <T> UResult<T> ok(T value) {
        return new UResult.Ok<>(value);
    }

    public static <T> UResult<T> err(String code, String message) {
        return new UResult.Err<>(code, message);
    }

    public static boolean isSuccess(UResult<?> result) {
        return result instanceof UResult.Ok;
    }

    public static boolean isFailure(UResult<?> result) {
        return result instanceof UResult.Err;
    }

    public static <T> T getOkValue(UResult<T> result) {
        if (result instanceof UResult.Ok<T> ok) {
            return ok.value();
        } else {
            throw new IllegalStateException("Result is not Ok");
        }
    }

    public static <T> String getErrMessage(UResult<T> result) {
        if (result instanceof UResult.Err<T> err) {
            return err.message();
        } else {
            throw new IllegalStateException("Result is not Err");
        }
    }

    public static <T> String getErrCode(UResult<T> result) {
        if (result instanceof UResult.Err<T> err) {
            return err.code();
        } else {
            throw new IllegalStateException("Result is not Err");
        }
    }
}
