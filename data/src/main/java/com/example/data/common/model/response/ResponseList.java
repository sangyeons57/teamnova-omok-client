package com.example.data.common.model.response;

import java.util.List;

public class ResponseList {
    private Meta meta;
    private List<Data> data;
    private Paging paging;
    private Error error;
    
    public boolean isSuccess() { return error == null; }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
