package com.example.myapplication;

import java.io.*;
import java.net.Socket;

public class JsonSender {
    public static void sendJsonToServer(String json, String serverIp, int serverPort) {
        try (Socket socket = new Socket(serverIp, serverPort);
             OutputStream out = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(out, true)) {
            writer.println(json);
            System.out.println("JSON успешно отправлен на сервер.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при отправке JSON на сервер.");
        }
    }

    public static void main(String[] args) {
        String json = "{\"name\": \"John\", \"age\": 30, \"city\": \"New York\"}";
        String serverIp = "192.168.1.100";
        int serverPort = 12345;

        sendJsonToServer(json, serverIp, serverPort);
    }
}
