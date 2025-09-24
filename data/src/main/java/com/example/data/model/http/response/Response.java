package com.example.data.model.http.response;

import java.util.Map;

public record Response (boolean isSuccess, int statusCode, String statusMessage, Map<String, String> headers, Map<String, Object> body) { }
