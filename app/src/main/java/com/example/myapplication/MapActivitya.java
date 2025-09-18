package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import androidx.navigation.ui.AppBarConfiguration;


import com.example.myapplication.databinding.ActivityMapActivityaBinding;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.mapview.MapView;

public class MapActivitya extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMapActivityaBinding binding;
    private Button MainView;
    private WebView mapview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        String Cord_X = extras.getString("Cord X");
        String Cord_Y = extras.getString("Cord Y");

        mapview = findViewById(R.id.mapview);
        mapview.loadUrl("https://map.red-atom.ru");
        MainView = findViewById(R.id.MainViev);
         MainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivitya.this, MainActivity.class);
                onStop();
                startActivity(intent);
            }
        });



    }
    @Override
    protected void onStart(){
        super.onStart();
        MapView mapView = findViewById(R.id.mapview);
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }
    @Override
    protected void onStop(){
        MapView mapView = findViewById(R.id.mapview);
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }
}