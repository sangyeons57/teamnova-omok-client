package com.example.feature_auth.login.presentation;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.data.auth.repository.DefaultLoginRepository;
import com.example.domain.auth.model.HelloWorldMessage;
import com.example.domain.auth.model.LoginAction;
import com.example.domain.auth.usecase.LoginActionUseCase;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * ViewModel orchestrating login related interactions while preserving the MVVM boundaries.
 */
public class LoginViewModel extends ViewModel {

    private static final String TAG = "LoginViewModel";

    private final LoginActionUseCase loginActionUseCase;
    private final ExecutorService executorService;

    private final MutableLiveData<LoginAction> loginAction = new MutableLiveData<>();
    private final MutableLiveData<String> helloWorldMessage = new MutableLiveData<>();
    private final MutableLiveData<String> helloWorldError = new MutableLiveData<>();

    public LoginViewModel() {
        this(new LoginActionUseCase(new DefaultLoginRepository()));
    }

    public LoginViewModel(@NonNull LoginActionUseCase loginActionUseCase) {
        this(loginActionUseCase, Executors.newSingleThreadExecutor());
    }

    public LoginViewModel(@NonNull LoginActionUseCase loginActionUseCase,
                          @NonNull ExecutorService executorService) {
        this.loginActionUseCase = Objects.requireNonNull(loginActionUseCase, "loginActionUseCase");
        this.executorService = Objects.requireNonNull(executorService, "executorService");
    }

    public LiveData<LoginAction> getLoginAction() {
        return loginAction;
    }

    public LiveData<String> getHelloWorldMessage() {
        return helloWorldMessage;
    }

    public LiveData<String> getHelloWorldError() {
        return helloWorldError;
    }

    public void onGuestLoginClicked() {
        try {
            executorService.execute(() -> {
                String json = ""
                        + "{"
                        + "  \"provider\": \"GUEST\","
                        + "  \"display_name\": \"새 유저\","
                        + "  \"issue_tokens\": true"
                        + "}";

                RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://bamsol.net/public/create-account.php")
                        .post(body)
                        .header("Content-Type", "application/json; charset=UTF-8")
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody == null) {
                        helloWorldError.postValue("Empty response body");
                        return;
                    }

                    String payload = responseBody.string();
                    Log.d(TAG, "onGuestLoginClicked: " + payload);
                    Log.d(TAG, "onGuestLoginClicked: " + responseBody);
                    helloWorldMessage.postValue(payload);
                } catch (IOException e) {
                    Log.e(TAG, "onGuestLoginClicked network error", e);
                    helloWorldError.postValue(resolveErrorMessage(e));
                }
            });
        } catch (RejectedExecutionException e) {
            Log.e(TAG, "onGuestLoginClicked execution rejected", e);
            helloWorldError.setValue(resolveErrorMessage(e));
        }
    }

    public void onGoogleLoginClicked() {
        loginAction.setValue(loginActionUseCase.loginWithGoogle());
    }

    public void onActionHandled() {
        loginAction.setValue(null);
    }

    @Override
    protected void onCleared() {
        executorService.shutdownNow();
        super.onCleared();
    }

    private String resolveErrorMessage(Exception exception) {
        String message = exception.getMessage();
        return (message == null || message.trim().isEmpty())
                ? "Failed to load hello world message"
                : message;
    }
}
