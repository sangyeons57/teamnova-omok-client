package com.example.teamnovaomok;

import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MyApp extends Application {
    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Auth", "packName= " + getApplicationContext().getPackageName());

        signInWithGoogle();
    }



    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public void signInWithGoogle() {
        final String WEB_CLIENT_ID = getString(R.string.default_web_client_id);

        Log.d("Auth" , "WEB_CLIENT_ID=" + WEB_CLIENT_ID);
        Log.d("Auth", "PKG=" + getApplicationContext().getPackageName());
        GetGoogleIdOption google = new GetGoogleIdOption.Builder()
                .setServerClientId(WEB_CLIENT_ID)
                .setFilterByAuthorizedAccounts(false)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(google)
                .build();
        Executor executor = Executors.newSingleThreadExecutor();
        CancellationSignal cancellationSignal = new CancellationSignal();
        CredentialManager cm = CredentialManager.create(getApplicationContext());
        cm.getCredentialAsync(
                getApplicationContext(),
                request,
                cancellationSignal,
                executor,
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Log.e("LoginFragment", "Exception", e);
                    }

                    @Override
                    public void onResult(GetCredentialResponse getCredentialResponse) {
                        try {
                            Credential credential = getCredentialResponse.getCredential();
                            if (credential instanceof CustomCredential ) {
                                // && GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(credential.getType())
                                Bundle data = ((CustomCredential) credential).getData();
                                GoogleIdTokenCredential gid = GoogleIdTokenCredential.createFrom(data);

                                String providerUserId = gid.getIdToken();
                                if (providerUserId == null || providerUserId.trim().isEmpty()) {
                                    providerUserId = gid.getId();
                                }

                                if (providerUserId == null || providerUserId.trim().isEmpty()) {
                                    Log.e("LoginFragment", "Google credential missing id token and subject");
                                    return;
                                }

                                Log.d("LoginFragment", "providerUserId:" + providerUserId);
                            } else {
                                Log.e("LoginFragment", "credential is not a CustomCredential");
                            }
                        } catch (Exception e) {
                            Log.e("LoginFragment", "Exception", e);
                        }
                    }

                }
        );
    }
}
