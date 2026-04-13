package com.smartcrop.model;

import java.util.List;

public class CropRecommendation {

    private String name;
    private String suitability;
    private String expectedYield;
    private String growthPeriod;
    private String waterRequirement;
    private List<String> reasons;
    private int score;

    // ✅ FULL CONSTRUCTOR (THIS WAS MISSING)
    public CropRecommendation(String name,
            String suitability,
            String expectedYield,
            String growthPeriod,
            String waterRequirement,
            List<String> reasons,
            int score) {
        this.name = name;
        this.suitability = suitability;
        this.expectedYield = expectedYield;
        this.growthPeriod = growthPeriod;
        this.waterRequirement = waterRequirement;
        this.reasons = reasons;
        this.score = score;
    }

    public CropRecommendation(String name,
            String suitability,
            String expectedYield,
            String growthPeriod,
            String waterRequirement,
            List<String> reasons) {
        this(name, suitability, expectedYield, growthPeriod, waterRequirement, reasons, 0);
    }

    // ✅ GETTERS
    public String getName() {
        return name;
    }

    public String getSuitability() {
        return suitability;
    }

    public String getExpectedYield() {
        return expectedYield;
    }

    public String getGrowthPeriod() {
        return growthPeriod;
    }

    public String getWaterRequirement() {
        return waterRequirement;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public int getScore() {
        return score;
    }

    // (optional setters if needed)
}