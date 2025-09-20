package com.example.data.model.http.response;

import java.util.List;
import java.util.Map;

public class Error {
    public static class FieldError {
        private String field;
        private String message;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    private String code;
    private int httpStatus;
    private String message;
    private String detail;
    private List<FieldError> fields;
    private Map<String, Object> extra;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public List<FieldError> getFields() {
        return fields;
    }

    public void setFields(List<FieldError> fields) {
        this.fields = fields;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return
                "code='" + code + '\'' + "\n" +
                "httpStatus=" + httpStatus + "\n" +
                "message='" + message + '\'' + "\n" +
                "detail='" + detail + '\'' + "\n" +
                "fields=" + fields + "\n" +
                "extra=" + extra;
    }
}
