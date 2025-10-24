package com.example.core_api.network.http;

import java.util.Map;

public record HttpResponse(boolean isSuccessful, int statusCode, String statusMessage, Map<String, String> headers, String body) { }
