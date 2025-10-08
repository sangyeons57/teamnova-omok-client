package com.example.application.port.out.realtime;

import java.util.concurrent.CompletableFuture;

public interface RealtimeRepository {
    CompletableFuture<String> hello(String payload);
    CompletableFuture<Boolean> auth(String accessToken);
    void joinMatch (String match);
    void readyInGameSession();
}
