package com.smartcrop.service;

import com.google.gson.*;
import com.smartcrop.model.CropRecommendation;

import java.util.List;

public class ReportService {

    private final SupabaseService db = SupabaseService.getInstance();
    private final WeatherService ws = new WeatherService();
    private final RecommendationEngine engine = new RecommendationEngine();
    private final Gson gson = new Gson();

    public void generateReport(String email) throws Exception {

        // 1. Get Soil Data
        JsonObject soil = db.getLatestSoilDataParsed(email);

        // 2. Get Weather
        JsonObject weather = ws.getWeather("Bengaluru");

        // 3. Get Recommendations
        List<CropRecommendation> crops = engine.generate(soil, weather);

        // 4. Build Report JSON
        JsonObject report = new JsonObject();

        report.addProperty("user_email", email);
        report.addProperty("type", "Comprehensive");
        report.addProperty("title", "Comprehensive Report");

        report.add("soil", soil);
        report.add("weather", weather);

        JsonArray cropArray = new JsonArray();
        for (CropRecommendation c : crops) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", c.getName());
            obj.addProperty("score", c.getScore());
            obj.addProperty("suitability", c.getSuitability());
            cropArray.add(obj);
        }

        report.add("recommendations", cropArray);

        // 5. SAVE TO SUPABASE
        db.insertRow("reports", gson.toJson(report));
    }
}