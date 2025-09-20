package com.example.data.common.model.response;

import java.util.List;

public class ResponseSingle {
    private Meta meta;
    private Data data;
    private Error error;

    public boolean isSuccess() { return error == null; }
    public boolean isError() { return error != null; }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

