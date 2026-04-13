package com.smartcrop.service;

import okhttp3.*;
import com.google.gson.*;

import java.io.IOException;

public class WeatherService {

    private static final String API_KEY = "dd8919be05bb2f1c67dd25f40bce184f"; // 🔥 paste here
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    private final OkHttpClient client = new OkHttpClient();

    public JsonObject getWeather(String city) throws IOException {

        String url = BASE_URL + "?q=" + city + "&appid=" + API_KEY + "&units=metric";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Failed: " + response);
        }

        String json = response.body().string();

        return JsonParser.parseString(json).getAsJsonObject();
    }

    public JsonObject getForecast(String city) throws IOException {

        String url = "https://api.openweathermap.org/data/2.5/forecast?q="
                + city + "&appid=" + API_KEY + "&units=metric";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Failed: " + response);
        }

        String json = response.body().string();

        return JsonParser.parseString(json).getAsJsonObject();
    }
}