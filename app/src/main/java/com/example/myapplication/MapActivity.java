package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;


public class MapActivity extends AppCompatActivity {

    private Button MainViev;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        String Cord_X = extras.getString("Cord X");
        String Cord_Y = extras.getString("Cord Y");
        MapKitFactory.setApiKey("4b33a376-85fa-4d02-9992-cd5464bfd70c");
        onPause();
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_map);
        MapView mapView = findViewById(R.id.mapview);
        MainViev = findViewById(R.id.MainViev);
        com.yandex.mapkit.map.MapObjectCollection yandexMap = mapView.getMapWindow().getMap().getMapObjects();
        PlacemarkMapObject placemark = yandexMap.addPlacemark(new Point(Double.parseDouble(Cord_Y.trim()), Double.parseDouble(Cord_X.trim())));
        mapView.getMapWindow().getMap().move(
                new CameraPosition(new Point(Double.parseDouble(Cord_Y.trim()), Double.parseDouble(Cord_X.trim())),13f,0f,0f)
        );
        MainViev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    // что нибудь


    @Override
    protected void onStart(){
        super.onStart();
        MapKitFactory.getInstance().onStart();
        MapView mapView = findViewById(R.id.mapview);
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