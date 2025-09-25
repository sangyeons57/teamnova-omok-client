package com.example.data.repository.user;

import android.util.Log;

import com.example.application.port.out.user.UserRepository;
import com.example.data.datasource.DefaultPhpServerDataSource;
import com.example.data.exception.RankingRemoteException;
import com.example.data.exception.UserDataRemoteException;
import com.example.data.model.http.request.Path;
import com.example.data.model.http.request.Request;
import com.example.data.model.http.response.Response;
import com.example.domain.user.entity.RankingEntry;
import com.example.domain.user.entity.User;
import com.example.domain.user.factory.UserFactory;
import com.example.domain.user.value.UserDisplayName;
import com.example.domain.user.value.UserId;
import com.example.domain.user.value.UserProfileIcon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserRepositoryImpl implements UserRepository {
    private static final String TAG = "UserRepositoryImpl";
    private static final int MAX_RANKING_ENTRIES = 500;
    private final DefaultPhpServerDataSource phpServerDataSource;

    public UserRepositoryImpl(DefaultPhpServerDataSource phpServerDataSource) {
        this.phpServerDataSource = phpServerDataSource;
    }
    @Override
    public String changeName(UserDisplayName newName) {
        try {
            Request request = Request.defaultRequest(Path.CHANGE_NAME);
            Map<String, Object> body = new HashMap<>();

            body.put("new_name", newName.getValue());
            request.setBody(body);

            Response response = phpServerDataSource.post(request);

            if(!response.isSuccess()) {
                String message = resolveErrorMessage(response);
                Log.e(TAG,"Failed to change name " + response.statusCode() + " | " + response.statusMessage() + " | " + message);
                return message;
            }

            return null;
        } catch (IOException exception) {
            Log.e(TAG,"Failed to change name", exception);
            return resolveErrorMessage(exception);
        }
    }

    @Override
    public boolean changeProfileIcon(UserProfileIcon newIcon) {
        try {
            Request request = Request.defaultRequest(Path.CHANGE_PROFILE_ICON);
            Map<String, Object> body = new HashMap<>();

            body.put("new_icon", newIcon.getValue());
            request.setBody(body);

            Response response = phpServerDataSource.post(request);

            if(!response.isSuccess()) {
                Log.e(TAG,"Failed to change profile icon " + response.statusCode() + " | " + response.statusMessage());
                return false;
            }

            return true;
        } catch (IOException exception) {
            Log.e(TAG,"Failed to change profile icon", exception);
            return false;
        }
    }

    @Override
    public List<RankingEntry> fetchRankingData() {
        try {
            Response response = phpServerDataSource.post(Request.defaultRequest(Path.RANKING_DATA));

            if (!response.isSuccess()) {
                Log.e(TAG, "Failed to fetch ranking data " + response.statusCode() + " | " + response.statusMessage());
                throw new RankingRemoteException("Failed to fetch ranking data");
            }

            return parseRankingEntries(response);
        } catch (IOException exception) {
            Log.e(TAG, "Failed to fetch ranking data", exception);
            throw new RankingRemoteException("Failed to fetch ranking data", exception);
        }
    }

    @Override
    public User fetchSelfData() {
        try {
            Response response = phpServerDataSource.post(Request.defaultRequest(Path.SELF_DATA));

            if (!response.isSuccess()) {
                Log.e(TAG, "Failed to fetch self data " + response.statusCode() + " | " + response.statusMessage());
                throw new UserDataRemoteException("Failed to fetch self data");
            }

            return mapUserFromBody(response.body());
        } catch (IOException exception) {
            Log.e(TAG, "Failed to fetch self data", exception);
            throw new UserDataRemoteException("Failed to fetch self data", exception);
        }
    }

    @Override
    public User fetchUserData(UserId userId) {
        Objects.requireNonNull(userId, "userId");
        try {
            Request request = Request.defaultRequest(Path.USER_DATA);
            Map<String, Object> body = new HashMap<>();
            body.put("user_id", userId.getValue());
            request.setBody(body);

            Response response = phpServerDataSource.post(request);

            if (!response.isSuccess()) {
                Log.e(TAG, "Failed to fetch user data " + response.statusCode() + " | " + response.statusMessage());
                throw new UserDataRemoteException("Failed to fetch user data");
            }

            return mapUserFromBody(response.body());
        } catch (IOException exception) {
            Log.e(TAG, "Failed to fetch user data", exception);
            throw new UserDataRemoteException("Failed to fetch user data", exception);
        }
    }

    private List<RankingEntry> parseRankingEntries(Response response) {
        List<RankingEntry> result = new ArrayList<>();
        Object payload = firstNonNull(response.body().get("rankings"),
                response.body().get("ranking"),
                response.body().get("data"),
                response.body().get("items"));

        if (!(payload instanceof List<?> entries)) {
            return result;
        }

        for (Object entryObj : entries) {
            if (!(entryObj instanceof Map<?, ?> map)) {
                continue;
            }

            Integer rank = toInteger(map.get("rank"));
            String userId = toString(map.get("user_id"));
            if (rank == null || userId == null) {
                continue;
            }

            String displayName = toString(map.get("display_name"));
            Integer profileIcon = toInteger(map.get("profile_icon_code"));
            String role = toString(map.get("role"));
            String status = toString(map.get("status"));
            Integer score = toInteger(map.get("score"));

            User user = UserFactory.createProfile(userId, displayName, profileIcon, role, status, score);
            result.add(new RankingEntry(rank, user));

            if (result.size() >= MAX_RANKING_ENTRIES) {
                break;
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private User mapUserFromBody(Map<String, Object> body) {
        Object payload = firstNonNull(body.get("user"), body.get("profile"), body.get("data"));
        Map<String, Object> source;
        if (payload instanceof Map<?, ?> map) {
            source = (Map<String, Object>) map;
        } else {
            source = body;
        }

        String userId = toString(source.get("user_id"));
        String displayName = toString(source.get("display_name"));
        Integer profileIcon = toInteger(source.get("profile_icon_code"));
        String role = toString(source.get("role"));
        String status = toString(source.get("status"));
        Integer score = toInteger(source.get("score"));

        return UserFactory.createProfile(userId, displayName, profileIcon, role, status, score);
    }

    private Object firstNonNull(Object... values) {
        for (Object value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private Integer toInteger(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String stringValue) {
            try {
                return Integer.parseInt(stringValue);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private String toString(Object value) {
        return value != null ? value.toString() : null;
    }

    private String resolveErrorMessage(Response response) {
        Object message = firstNonNull(response.body().get("message"), response.statusMessage());
        return normalizeMessage(message);
    }

    private String resolveErrorMessage(IOException exception) {
        return normalizeMessage(exception.getMessage());
    }

    private String normalizeMessage(Object value) {
        String message = value != null ? value.toString() : null;
        if (message == null || message.trim().isEmpty()) {
            return "Failed to change display name";
        }
        return message;
    }
}
