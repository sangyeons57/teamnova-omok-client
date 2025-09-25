package com.example.teamnovaomok;

import android.app.Application;
import android.os.Build;
import androidx.annotation.RequiresApi;

public class MyApp extends Application {
    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
