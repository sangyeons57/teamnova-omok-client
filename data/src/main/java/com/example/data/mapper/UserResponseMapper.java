package com.example.data.mapper;

import android.util.Log;

import com.example.domain.user.entity.RankingEntry;
import com.example.domain.user.entity.User;
import com.example.domain.user.factory.UserFactory;
import com.example.domain.user.value.UserDisplayName;
import com.example.domain.user.value.UserId;
import com.example.domain.user.value.UserRank;
import com.example.domain.user.value.UserScore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Maps raw backend payloads into domain user aggregates or ranking entries.
 */
public class UserResponseMapper {

    public User mapUserProfile(Map<String, Object> body) {
        Map<String, Object> source = resolveUserMap(body);
        return UserFactory.createProfile(
                stringValue(source.get("user_id")),
                stringValue(source.get("display_name")),
                toInteger(source.get("profile_icon_code")),
                stringValue(source.get("role")),
                stringValue(source.get("status")),
                toInteger(source.get("score"))
        );
    }

    public List<RankingEntry> mapRankingEntries(Map<String, Object> body, int maxEntries) {
        if (body == null || body.isEmpty()) {
            return Collections.emptyList();
        }
        Object payload = firstNonNull(
                body.get("ranking"),
                body.get("rankings"),
                body.get("data"),
                body.get("items"));

        if (!(payload instanceof List<?> entries)) {
            return Collections.emptyList();
        }

        int limit = Math.max(1, maxEntries);
        List<RankingEntry> result = new ArrayList<>(Math.min(limit, entries.size()));
        for (Object entryObj : entries) {
            RankingEntry entry = mapRankingEntry(entryObj);
            if (entry != null) {
                result.add(entry);
                if (result.size() >= limit) {
                    break;
                }
            }
        }
        return result;
    }

    private RankingEntry mapRankingEntry(Object entryObj) {
        if (!(entryObj instanceof Map<?, ?> raw)) {
            return null;
        }

        Map<String, Object> map = cast(raw);
        Integer rankValue = toInteger(map.get("rank"));
        String userIdValue = stringValue(map.get("user_id"));
        if (rankValue == null || rankValue <= 0 || userIdValue == null || userIdValue.trim().isEmpty()) {
            return null;
        }

        UserRank rank = UserRank.of(rankValue);
        UserId userId = UserId.of(userIdValue);
        UserDisplayName displayName = UserDisplayName.of(stringValueOrDefault(map.get("display_name"), ""));
        UserScore score = UserScore.of(toIntegerOrDefault(map.get("score"), 0));

        return new RankingEntry(rank, userId, displayName, score);
    }

    private Map<String, Object> resolveUserMap(Map<String, Object> body) {
        try {
            JSONObject jsonObject = new JSONObject(valueOf(body, "user").toString());
            Map<String, Object> result = new HashMap<>();
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                result.put(key, jsonObject.get(key));
            }

            return result;
        } catch (ClassCastException exception) {
            Log.e("UserResponseMapper", "error:" + exception.getMessage());
            return Collections.emptyMap();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private Object firstNonNull(Object... values) {
        if (values == null) {
            return null;
        }
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
        if (value instanceof String string) {
            try {
                return Integer.parseInt(string);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private int toIntegerOrDefault(Object value, int defaultValue) {
        Integer result = toInteger(value);
        return result != null ? result : defaultValue;
    }

    private String stringValue(Object value) {
        return value != null ? value.toString() : null;
    }

    private String stringValueOrDefault(Object value, String defaultValue) {
        String result = stringValue(value);
        return result != null ? result : defaultValue;
    }

    private Object valueOf(Map<String, Object> map, String key) {
        return map != null ? map.get(key) : null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> cast(Map<?, ?> map) {
        return (Map<String, Object>) map;
    }
}
