package com.example.data.repository.user;

import android.util.Log;

import com.example.application.port.out.user.UserRepository;
import com.example.data.datasource.DefaultPhpServerDataSource;
import com.example.data.exception.LogoutRemoteException;
import com.example.data.model.http.request.Path;
import com.example.data.model.http.request.Request;
import com.example.data.model.http.response.Response;
import com.example.domain.user.value.UserDisplayName;
import com.example.domain.user.value.UserProfileIcon;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserRepositoryImpl implements UserRepository {
    private final DefaultPhpServerDataSource phpServerDataSource;

    public UserRepositoryImpl(DefaultPhpServerDataSource phpServerDataSource) {
        this.phpServerDataSource = phpServerDataSource;
    }
    @Override
    public boolean changeName(UserDisplayName newName) {
        try {
            Request request = Request.defaultRequest(Path.CHANGE_NAME);
            Map<String, Object> body = new HashMap<>();

            body.put("new_name", newName.getValue());
            request.setBody(body);

            Response response = phpServerDataSource.post(request);

            if(!response.isSuccess()) {
                Log.e("UserRepositoryImpl","Failed to logout" + response.statusCode() + " | " + response.statusMessage());
                return false;
            }

            return true;
        } catch (IOException exception) {
            Log.e("UserRepositoryImpl","Failed to logout" + exception);
            return false;
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
                Log.e("UserRepositoryImpl","Failed to logout" + response.statusCode() + " | " + response.statusMessage());
                return false;
            }

            return true;
        } catch (IOException exception) {
            Log.e("UserRepositoryImpl","Failed to logout" + exception);
            return false;
        }
    }
}
