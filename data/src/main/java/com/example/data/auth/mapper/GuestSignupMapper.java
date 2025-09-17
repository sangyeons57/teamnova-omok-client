package com.example.data.auth.mapper;

import com.example.data.auth.model.GuestSignupResponse;
import com.example.domain.auth.model.GuestSignupResult;
import com.example.domain.auth.model.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Maps guest sign-up transport models into domain representations.
 */
public class GuestSignupMapper {

    public GuestSignupResult toDomain(GuestSignupResponse response) {
        if (response == null || response.getMessage() == null || response.getMessage().isEmpty()) {
            return GuestSignupResult.failure("Empty response from server.");
        }

        try {
            JSONObject json = new JSONObject(response.getMessage());

            boolean isSuccess = json.optBoolean("success", false);

            if (isSuccess) {
                JSONObject userJson = json.getJSONObject("user");
                User user = new User(
                        userJson.getString("user_id"),
                        userJson.getString("display_name"),
                        userJson.getString("profile_icon_code"),
                        userJson.getString("role"),
                        userJson.getString("status"),
                        userJson.getInt("score")
                );

                String accessToken = json.getString("access_token");
                String refreshToken = json.getString("refresh_token");

                return GuestSignupResult.success(user, accessToken, refreshToken);
            } else {
                String errorMessage = json.optString("message", "Unknown error.");
                return GuestSignupResult.failure(errorMessage);
            }

        } catch (JSONException e) {
            // Log the exception for debugging purposes
            // e.printStackTrace();
            return GuestSignupResult.failure("Failed to parse server response.");
        }
    }
}
