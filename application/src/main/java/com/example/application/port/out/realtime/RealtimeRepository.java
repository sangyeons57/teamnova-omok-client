package com.example.application.port.out.realtime;

public interface RealtimeRepository {
    void auth(String accessToken);
    void reconnectTcp(String accessToken, String gameSessionId);
    void joinMatch(String match);
    void leaveMatch();
    void readyInGameSession();
    void placeStone(int x, int y);
    void postGameDecision(PostGameDecisionOption decision);
}
