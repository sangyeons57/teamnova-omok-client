package com.example.data.mapper;

import com.example.data.exception.GuestSignupRemoteException;
import com.example.data.model.http.response.ResponseSingle;
import com.example.domain.identity.entity.Identity;
import com.example.domain.user.value.AccessToken;
import com.example.domain.user.value.RefreshToken;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Maps guest sign-up transport models into domain representations.
 */
public class IdentityMapper {

    public Identity toIdentity(ResponseSingle response) {
        try {
            JSONObject json = new JSONObject(response.getData().getPayload());

            String accessToken = json.getString("access_token");
            String refreshToken = json.getString("refresh_token");
            String userId = json.getString("user_id");

            return new Identity(
                    userId,
                    new AccessToken(accessToken),
                    new RefreshToken(refreshToken));
        } catch (JSONException exception) {
            throw new GuestSignupRemoteException("Failed to parse server response.", exception);
        }
    }
}
