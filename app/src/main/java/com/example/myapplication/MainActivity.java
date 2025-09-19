package com.example.myapplication;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;

import java.io.IOException;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements LocListenerInterface {
    // Выделение памяти под UX/UI объекты
    private TextView textOperatorName;
    private TextView textValueConnect;
    private TextView textTypeConnect;
    private TextView textX;
    private TextView textY;
    private TextView textUpload;
    private TextView textDownload;
    private TextView textStatusPost;
    private TextView textSignalStatus;

    private Button Reload;

    private View MapLayot;
    private Button SendPos;
    private Switch Auto_Man;
    // Подключение и выделение памяти модулей
    private LocationManager locationManager;
    private GetCoordsListener myLoclistener;
    public GetOperators operatorInfoHelper;
    // Базовые переменные для JSON файла

    public String coord_X;
    public String coord_Y;

    public double coord_X_Double;

    public double coord_Y_Double;

    public String operator_name;
    public String lvl_signal;
    public String lvl_signal_type;
    public int download;
    public int upload;
    boolean isMapKitInitialized = false;

    // Главный метод
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isMapKitInitialized) {

            MapKitFactory.initialize(this);
            isMapKitInitialized = true;
        }
        setContentView(R.layout.activity_main);
        // Задержка 2 секунды перед переключением на основной интерфейс


        //проверка включенности гелокации
        CheckLocation();
        // Подключение модуля (создание экзепляра класса GetOperators)
        operatorInfoHelper = new GetOperators(this);

        // Запрос к модулю для получения названия оператора связи
        String operatorName = operatorInfoHelper.getOperatorIDtoName();

        // Вывод названия оператора связи
        textOperatorName = findViewById(R.id.textOperatorName);
        operator_name = operatorName;
        textOperatorName.setText(operator_name);


        // Вывод силы сигнала

        textValueConnect = findViewById(R.id.textValueConnect);
        textTypeConnect = findViewById(R.id.signalTypeConnect);

        // Подключение модуля (создание экзепляра класса GetCoordsListener)
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        myLoclistener = new GetCoordsListener();
        myLoclistener.setLocListenerInterface((LocListenerInterface) this);

        // Запрос разрешения
        CheckPremission();
        if(startLocationUpdates()){
            // Получение уровня сигнала
            lvl_signal = operatorInfoHelper.getlvl();
            lvl_signal_type = operatorInfoHelper.getlvl_type();
            textValueConnect.setText(String.valueOf(lvl_signal));
            textTypeConnect.setText(lvl_signal_type);
        }



        // Вывод координат
        textX = findViewById(R.id.textX);
        textY = findViewById(R.id.textY);
        textX.setText("Загрузка...");
        textY.setText("Загрузка...");
        //Текстовая оценка сигнала
        fTextSignalStatus();
        // Получение данных о скорости соединения
        {
            textUpload = findViewById(R.id.textUpload);
            textDownload = findViewById(R.id.textDownload);

            int downSpeed = 0;
            int upSpeed = 0;

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            Network network = cm.getActiveNetwork();

            if (network != null) {
                // Получаем информацию о возможностях сети
                NetworkCapabilities nc = cm.getNetworkCapabilities(network);

                if (nc != null) {
                    // Проверяем, есть ли интернет
                    if (nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                        // Проверяем, подключена ли сеть
                        if (nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                            // Получаем скорости
                            downSpeed = nc.getLinkDownstreamBandwidthKbps(); // Скорость скачивания
                            upSpeed = nc.getLinkUpstreamBandwidthKbps();     // Скорость отправки
                        }
                    }
                }
            }

            download = downSpeed;
            upload = upSpeed;
            textUpload.setText(upload + " kbps");
            textDownload.setText(download + " kbps");
        }


        //определение поля сообщения об отправке
        textStatusPost = findViewById(R.id.textStatusPost);
        //Определение кнопок
        Reload = (Button) findViewById(R.id.Reload);
        SendPos = (Button) findViewById(R.id.SendPos);
        ImageView Mapviev =  findViewById(R.id.logo);
        MapLayot = (View) findViewById(R.id.MapConstLayout);
        MapView mapView = findViewById(R.id.mapview);
        MapObjectCollection yandexMap = mapView.getMapWindow().getMap().getMapObjects();

        Mapviev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();

                MapLayot.setVisibility(View.VISIBLE);
                Reload.setVisibility(View.INVISIBLE);
                SendPos.setVisibility(View.INVISIBLE);
//                String url = "https://map.red-atom.ru";
//                Intent openPage = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(openPage);
//                Intent map = new Intent(MainActivity.this, MapActivity.class);
//                map.putExtra("Cord X",coord_X);
//                map.putExtra("Cord Y",coord_Y);
//                v.getContext().startActivity(map);
            }


        });

        // Обработка события нажатия на обновить
        Reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }


        });


        //Обработка события нажатия на отправить данные
        SendPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                update();
                Date date = new Date();

                MyData data = new MyData(coord_X, coord_Y, operator_name, lvl_signal, lvl_signal_type, download, upload, date.toString());

                // Преобразование в JSON
                Gson gson = new Gson();
                String json = gson.toJson(data);

                textStatusPost.setText("Отправленно");

                if (lvl_signal_type.equals("LTE") && Integer.valueOf(lvl_signal) > -110) {
                    //Отправка ранее сохраненный файлов
                    Gson gsonSaved = new Gson();
                    Send(gson.toJson(GetText("storage.json")));

                    //Отправка данных на сервер
                    Send(json);

                } else {
                    textStatusPost.setText("Нет связи. Данные измерений сохранены в файл");
                    //Сохранение данных в файл
                    SaveData(json);
                }
            }
        });
        //Свич на отправку атоматически
        Auto_Man = findViewById(R.id.Auto_Man);
        Auto_Man.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Auto_Man.isChecked()) {
                    Timer();
                } else {
                    textStatusPost.setText("Не Автоматическая отправка");
                    Timer().cancel();
                }

            }
        });
    }

    //получение данных из файла
    private String GetText(String FileName) {
        try {

            FileInputStream fin = null;
            try {
                fin = openFileInput(FileName);
                byte[] bytes = new byte[fin.available()];
                fin.read(bytes);
                String text = new String(bytes);
                return text;
            } catch (IOException ex) {

                makeText(this, ex.getMessage(), LENGTH_SHORT).show();
            } finally {

                try {
                    if (fin != null)
                        fin.close();

                } catch (IOException ex) {

                    makeText(this, ex.getMessage(), LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    //timer для автоматичсекой отправки
    private Timer Timer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textStatusPost.setText("Автоматическая отправка");
                    }
                });
                update();
                Date date = new Date();
                MyData data = new MyData(coord_X, coord_Y, operator_name, lvl_signal, lvl_signal_type, download, upload, date.toString());
                // Преобразование в JSON
                Gson gson = new Gson();
                String json = gson.toJson(data);
                //Проверка на силу сигнала
                if (lvl_signal_type.equals("LTE") && Integer.valueOf(lvl_signal) > -110) {
                    //Отправка данных на сервер
                    Send(json);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textStatusPost.setText("Сохранение в файл");
                        }
                    });
                    SaveData(json);

                }
            }
        }, 0, 60000); // Запуск без задержки, повторение ежесекундно.
        return timer;
    }

    //Обновление данных
    private void update() {

        String operatorName = operatorInfoHelper.getOperatorIDtoName();
        operator_name = operatorName;
        textOperatorName.setText(operator_name);

        textValueConnect = findViewById(R.id.textValueConnect);
        textTypeConnect = findViewById(R.id.signalTypeConnect);
        lvl_signal = operatorInfoHelper.getlvl();
        lvl_signal_type = operatorInfoHelper.getlvl_type();
        textValueConnect.setText(String.valueOf(lvl_signal));
        textTypeConnect.setText(lvl_signal_type);

        // Текстовая оценка сигнала
        fTextSignalStatus();

        int downSpeed = 0;
        int upSpeed = 0;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();

        if (network != null) {
            // Получаем информацию о возможностях сети
            NetworkCapabilities nc = cm.getNetworkCapabilities(network);

            if (nc != null) {
                // Проверяем, есть ли интернет
                if (nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    // Проверяем, подключена ли сеть
                    if (nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                        // Получаем скорости
                        downSpeed = nc.getLinkDownstreamBandwidthKbps(); // Скорость скачивания
                        upSpeed = nc.getLinkUpstreamBandwidthKbps();     // Скорость отправки
                    }
                }
            }
        }

        textUpload.setText(String.valueOf(upSpeed) + " kbps");
        upload = upSpeed;

        textDownload.setText(String.valueOf(downSpeed) + " kbps");
        download = downSpeed;
    }

    //Проверка разрешения на геолокации и её включения
    private void CheckLocation() {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        //запрос на включение геолокации
        if (!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(this)
                    .setMessage("gps_network_not_enabled")
                    .setPositiveButton("open_location_settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }


                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    //Сохранение данных в файл
    private void SaveData(String json) {
        try {
            FileOutputStream fos = openFileOutput("storage.json", MODE_APPEND);
            if (json != null) {
                fos.write(json.getBytes());
            }
            fos.close();

        } catch (IOException ioException) {
        }
    }

    //Отправка данных
    public void Send(String json) {


        String serverUrl = "https://red-atom.ru/api/data";

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody body = RequestBody.create(json.getBytes());
                    Request.Builder requestBuilder = new Request.Builder().url(serverUrl).post(body);
                    Request request = requestBuilder.build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) {
                            throw new IOException("Запрос к серверу не был успешен: " +
                                    response.code() + " " + response.message());
                        }
                        textStatusPost.setText(response.body().string());
                        if (response.body().string() == "Получено") {
                            //Очистка файла после получения(отправки) данных
                            FileOutputStream fos = openFileOutput("storage.json", MODE_PRIVATE);
                        }

                    } catch (IOException e) {
                        textStatusPost.setText("Ошибка подключения: " + e);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }
    //Текстовоя Оценка качества сигнала
    private void fTextSignalStatus() {
        textSignalStatus = findViewById(R.id.textSignalStatus);

        // Check if lvl_signal is null or empty first
        if (lvl_signal == null || lvl_signal.isEmpty()) {
            textSignalStatus.setText("Сигнал не доступен");
            return; // Exit early to avoid the crash
        }
        try {
            int signalLevel = Integer.parseInt(lvl_signal);

            if (signalLevel < -110) {
                textSignalStatus.setText("Нет сети");
            } else if (signalLevel < -100) { // && signalLevel >= -110 is redundant
                textSignalStatus.setText("Очень плохой");
            } else if (signalLevel < -90) { // && signalLevel >= -99 is redundant
                textSignalStatus.setText("Нестабильный");
            } else if (signalLevel < -80) { // && signalLevel >= -89 is redundant
                textSignalStatus.setText("Неплохой");
            } else if (signalLevel < -65) { // && signalLevel >= -79 is redundant
                textSignalStatus.setText("Стабильная связь");
            } else {
                textSignalStatus.setText("Идеальный");
            }
        } catch (NumberFormatException e) {
            // Handle case where lvl_signal is not a valid number
            textSignalStatus.setText("Ошибка сигнала");
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100 && grantResults[0] == RESULT_OK)
        {
            CheckPremission();
        }
    }

    private void CheckPremission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 2000, 10, myLoclistener);

        }
    }
    @Override
    public void OnLocationChange(Location loc) {
        coord_X = String.valueOf(loc.getLongitude());
        coord_Y = String.valueOf(loc.getLatitude());
        coord_X_Double = loc.getLongitude();
        coord_Y_Double =loc.getLatitude();
        textX.setText(String.format("%.6f", coord_X_Double));
        textY.setText(String.format("%.6f", coord_Y_Double));

        // Логи для отладки
        Log.d("Location", "Longitude (X): " + coord_X_Double);
        Log.d("Location", "Latitude (Y): " + coord_Y_Double);
        Log.d("Location", "Accuracy: " + loc.getAccuracy() + "m");
        updateMapWithLocation(coord_Y_Double, coord_X_Double);
    }
    private void updateMapWithLocation(double lat, double lng) {
        MapView mapView = findViewById(R.id.mapview);
        MapObjectCollection yandexMap = mapView.getMapWindow().getMap().getMapObjects();
        PlacemarkMapObject placemark = yandexMap.addPlacemark(new Point(coord_Y_Double, coord_X_Double));
        mapView.getMapWindow().getMap().move(
                new CameraPosition(new Point(coord_Y_Double, coord_X_Double),16f,0f,0f)
        );
    }
    @Override
    protected void onStart(){
        super.onStart();
        MapView mapView = findViewById(R.id.mapview);
        MapKitFactory.getInstance().onStart();
        if (mapView != null) {
            mapView.onStart();
        }

    }
    @Override
    protected void onStop(){
        super.onStop();
        MapView mapView = findViewById(R.id.mapview);
        if (mapView != null) {
            mapView.onStop();
        }
        MapKitFactory.getInstance().onStop();
    }

    private boolean startLocationUpdates() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                // Запрос обновлений местоположения
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000,      // интервал в миллисекундах
                        1,         // минимальное расстояние в метрах
                        myLoclistener
                );

                // Также можно получить последнее известное местоположение
                Location lastKnownLocation = locationManager.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null) {
                    OnLocationChange(lastKnownLocation);
                }
                return true;
            }
        } catch (SecurityException e) {
            Log.e("Location", "SecurityException: " + e.getMessage());
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Останавливаем обновления местоположения для экономии батареи
        if (locationManager != null) {
            locationManager.removeUpdates(myLoclistener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Возобновляем обновления при возвращении в приложение
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

}