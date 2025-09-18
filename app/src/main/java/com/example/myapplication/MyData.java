package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyData {
    @SerializedName("X")
    private String X;
    @SerializedName("Y")
    private String Y;
    @SerializedName("OperatorName")
    private String OperatorName;
    @SerializedName("LevelSignal")
    private String LevelSignal;
    @SerializedName("lvl_signal_type")
    private String lvl_signal_type;
    @SerializedName("Download")
    private int Download;
    @SerializedName("Upload")
    private int Upload;
    @SerializedName("date")
    private String Date;

    public MyData(String x, String y, String operatorName, String levelSignal, String lvl_signal_type, int download, int upload, String date ) {
        this.X = x;
        this.Y = y;
        this.OperatorName = operatorName;
        this.LevelSignal = levelSignal;
        this.lvl_signal_type = lvl_signal_type;
        this.Download = download;
        this.Upload = upload;
        this.Date = date;
    }

    private OkHttpClient client;

    public void MyNetworkHelper() {
        client = new OkHttpClient();
    }

    public String sendPostRequest(String url, String json) throws IOException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

}
