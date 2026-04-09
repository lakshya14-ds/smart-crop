package com.smartcrop.service;

import okhttp3.*;

import java.io.IOException;

public class SupabaseService {

    private static final String BASE_URL = "https://YOUR_PROJECT.supabase.co/rest/v1/";
    private static final String API_KEY = "YOUR_API_KEY";

    private final OkHttpClient client = new OkHttpClient();

    public void insertSoilData(String json) throws IOException {

        RequestBody body = RequestBody.create(
                json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(BASE_URL + "soil_data")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

        System.out.println(response.body().string());
    }
}