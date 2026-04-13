package com.smartcrop.service;

import com.smartcrop.model.CropRecommendation;
import com.google.gson.JsonObject;

import java.util.*;

public class RecommendationEngine {

    public List<CropRecommendation> generate(JsonObject soil, JsonObject weather) {

        List<CropRecommendation> list = new ArrayList<>();

        // ✅ SAFE EXTRACTION (no crash)
        double ph = getSafe(soil, "ph");
        double moisture = getSafe(soil, "moisture");
        double nitrogen = getSafe(soil, "nitrogen");

        double temp = 0;
        double rain = 0;

        if (weather != null && weather.has("main")) {
            temp = weather.getAsJsonObject("main").get("temp").getAsDouble();
        }

        if (weather != null && weather.has("rain")
                && weather.getAsJsonObject("rain").has("1h")) {
            rain = weather.getAsJsonObject("rain").get("1h").getAsDouble();
        }

        // 🌾 RICE
        int riceScore = 0;
        List<String> riceReasons = new ArrayList<>();

        if (ph < 6.5) {
            riceScore += 25;
            riceReasons.add("Suitable acidic soil (pH < 6.5)");
        }
        if (moisture > 20) {
            riceScore += 25;
            riceReasons.add("High moisture supports rice growth");
        }
        if (rain > 5) {
            riceScore += 25;
            riceReasons.add("Rainfall is ideal for rice");
        }
        if (temp > 25) {
            riceScore += 25;
            riceReasons.add("Warm temperature is favorable");
        }

        list.add(buildCrop("Rice", riceScore, "120 days", "High", riceReasons));

        // 🌽 MAIZE
        int maizeScore = 0;
        List<String> maizeReasons = new ArrayList<>();

        if (ph >= 6 && ph <= 7.5) {
            maizeScore += 25;
            maizeReasons.add("Neutral soil ideal for maize");
        }
        if (moisture < 40) {
            maizeScore += 20;
            maizeReasons.add("Moderate moisture suitable");
        }
        if (temp >= 20 && temp <= 30) {
            maizeScore += 30;
            maizeReasons.add("Optimal temperature range");
        }
        if (rain < 10) {
            maizeScore += 25;
            maizeReasons.add("Low rainfall preferred");
        }

        list.add(buildCrop("Maize", maizeScore, "90 days", "Medium", maizeReasons));

        // 🌱 WHEAT
        int wheatScore = 0;
        List<String> wheatReasons = new ArrayList<>();

        if (ph >= 6.5 && ph <= 7.5) {
            wheatScore += 25;
            wheatReasons.add("Neutral soil ideal");
        }
        if (moisture < 30) {
            wheatScore += 25;
            wheatReasons.add("Low moisture preferred");
        }
        if (temp < 25) {
            wheatScore += 25;
            wheatReasons.add("Cool temperature suitable");
        }
        if (rain < 5) {
            wheatScore += 25;
            wheatReasons.add("Low rainfall required");
        }

        list.add(buildCrop("Wheat", wheatScore, "110 days", "Low", wheatReasons));

        // 🌱 BONUS: PULSES (uses nitrogen insight)
        int pulseScore = 0;
        List<String> pulseReasons = new ArrayList<>();

        if (nitrogen < 30) {
            pulseScore += 40;
            pulseReasons.add("Improves low nitrogen soil");
        }
        if (ph >= 6 && ph <= 7.5) {
            pulseScore += 30;
            pulseReasons.add("Good soil pH for pulses");
        }
        if (moisture < 35) {
            pulseScore += 30;
            pulseReasons.add("Moderate moisture is suitable");
        }

        list.add(buildCrop("Pulses", pulseScore, "85 days", "Low", pulseReasons));

        // 🔥 SORT BY SCORE (DESCENDING)
        list.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

        return list;
    }

    // 🧠 BUILD OBJECT
    private CropRecommendation buildCrop(String name, int score, String period, String water, List<String> reasons) {

        // cap score at 100
        score = Math.min(score, 100);

        String suitability;
        if (score >= 70)
            suitability = "excellent";
        else if (score >= 50)
            suitability = "good";
        else
            suitability = "poor";

        if (reasons.isEmpty()) {
            reasons.add("General compatibility with conditions");
        }

        reasons.add("Final score: " + score + "% match");

        return new CropRecommendation(
                name,
                suitability,
                "Medium",
                period,
                water,
                reasons,
                score);
    }

    // ✅ SAFE VALUE FETCHER
    private double getSafe(JsonObject obj, String key) {
        return (obj != null && obj.has(key)) ? obj.get(key).getAsDouble() : 0;
    }
}