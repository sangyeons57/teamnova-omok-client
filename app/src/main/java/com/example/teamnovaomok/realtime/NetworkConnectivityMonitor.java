package com.example.teamnovaomok.realtime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.core_api.event.AppEventBus;
import com.example.core_api.event.TcpConnectRequestedEvent;

import java.util.Objects;

public final class NetworkConnectivityMonitor {

    private static final String TAG = "NetworkConnectivityMonitor";

    private final ConnectivityManager connectivityManager;
    private final AppEventBus eventBus;
    private final ConnectivityManager.NetworkCallback networkCallback;
    private final Handler handler;

    public NetworkConnectivityMonitor(@NonNull Context context,
                                      @NonNull AppEventBus eventBus) {
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.eventBus = Objects.requireNonNull(eventBus, "eventBus");
        this.handler = new Handler(Looper.getMainLooper());
        this.networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                eventBus.postAsync(TcpConnectRequestedEvent.INSTANCE);
            }
        };
    }

    @SuppressLint("MissingPermission")
    public void start() {
        if (connectivityManager == null) {
            return;
        }
        try {
            connectivityManager.registerDefaultNetworkCallback(networkCallback, handler);
            if (connectivityManager.getActiveNetwork() != null) {
                eventBus.postAsync(TcpConnectRequestedEvent.INSTANCE);
            }
        } catch (RuntimeException e) {
            Log.w(TAG, "registerDefaultNetworkCallback failed", e);
        }
    }

    public void stop() {
        if (connectivityManager == null) {
            return;
        }
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        } catch (RuntimeException e) {
            Log.w(TAG, "unregisterNetworkCallback failed", e);
        }
    }
}
