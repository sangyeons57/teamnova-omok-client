package com.example.infra.http.php;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class LoggingInterceptor implements Interceptor {

    private static final String INDENT = "    ";
    private static final String TAG = "LoggingInterceptor";
    private static final int MAX_LOG_BYTES = 1024 * 4; // 4KB만 미리보기

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request(); logRequest(request);
        Response response = chain.proceed(request); logResponse(response);
        return response;
    }



    private static void logRequest(Request request) {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP Request\n")
                .append(INDENT).append("URL: ").append(request.url()).append('\n')
                .append(INDENT).append("Method: ").append(request.method()).append('\n')
                .append("Headers:\n").append(formatHeaders(request.headers())).append('\n')
                .append("Body:\n").append(formatBody(readRequestBody(request)));
        Log.d(TAG, builder.toString());
    }

    private static void logResponse(Response response) {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP Response\n")
                .append(INDENT).append("Code: ").append(response.code()).append('\n')
                .append(INDENT).append("Message: ").append(response.message()).append('\n')
                .append("Headers:\n").append(formatHeaders(response.headers())).append('\n')
                .append("Body:\n").append(formatBody(readResponseBody(response)));
        Log.d(TAG, builder.toString());
    }

    private static String formatHeaders(Headers headers) {
        if (headers == null || headers.size() == 0) {
            return INDENT + "(none)";
        }

        StringBuilder builder = new StringBuilder();
        for (String name : headers.names()) {
            builder.append(INDENT)
                    .append(name)
                    .append(": ")
                    .append(String.join(", ", headers.values(name)))
                    .append('\n');
        }

        if (builder.length() > 0) {
            builder.setLength(builder.length() - 1);
        }

        return builder.toString();
    }

    private static String formatBody(String body) {
        if (body == null) {
            return INDENT + "(null)";
        }
        if (body.isEmpty()) {
            return INDENT + "(empty)";
        }

        String[] lines = body.split("\\r?\\n", -1);
        StringBuilder builder = new StringBuilder();
        for (String line : lines) {
            builder.append(INDENT).append(line).append('\n');
        }

        if (builder.length() > 0) {
            builder.setLength(builder.length() - 1);
        }

        return builder.toString();
    }

    private static String readRequestBody(Request request) {
        try {
            RequestBody body = request.body();
            assert body != null;
            Buffer buffer = new Buffer();
            body.writeTo(buffer);

            MediaType mediaType = body.contentType();

            Charset charset = (mediaType != null && mediaType.charset() != null) ? mediaType.charset() : StandardCharsets.UTF_8;
            assert charset != null;

            if (mediaType != null
                    && (mediaType.type().equals("multipart")
                    || mediaType.type().equals("application")
                    && mediaType.subtype().contains("octet-stream"))
            ) {
                return "<" + mediaType + " binary body omitted>";
            } else {
                return buffer.readString(charset);
            }
        } catch (IOException e) {
            return "<error reading request body>";
        }
    }

    private static String readResponseBody(Response response) {
        try {
            if(response.body() == null) return "<no body>";

            ResponseBody peeked = response.peekBody(MAX_LOG_BYTES);
            String snippet = peeked.string();

            if(snippet.length() == MAX_LOG_BYTES){
                return snippet + "\n... (truncated) ...";
            }

            return snippet;
        } catch (Throwable t) {
            return "<peek failed: " + t.getClass().getSimpleName() + ": " + t.getMessage() + ">";
        }
    }
}
