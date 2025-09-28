package com.example.application.port.result;

public class GetOrCreateResults {
    public static <T> GetOrCreateResult<T> ok(T value, boolean isNew) {
        return new GetOrCreateResult.Ok<>(value, isNew);
    }

    public static boolean isSuccess(GetOrCreateResult<?> result) {
        return result instanceof GetOrCreateResult.Ok;
    }


    public static <T> T getOkValue(GetOrCreateResult<T> result) {
        if (result instanceof GetOrCreateResult.Ok<T> ok) {
            return ok.value();
        } else {
            throw new IllegalStateException("Result is not Ok");
        }
    }
}
