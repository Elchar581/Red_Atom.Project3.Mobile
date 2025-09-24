package com.example.myapplication;

import android.app.Application;
import android.util.Log;

import com.yandex.mapkit.MapKitFactory;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Устанавливаем API ключ для Yandex MapKit
        MapKitFactory.setApiKey("4b33a376-85fa-4d02-9992-cd5464bfd70c");
    }
}