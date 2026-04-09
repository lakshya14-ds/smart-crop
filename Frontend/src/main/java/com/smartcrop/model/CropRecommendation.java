package com.smartcrop.model;

import java.util.List;

public class CropRecommendation {
    private final String name;
    private final String suitability; // excellent, good, fair
    private final String expectedYield;
    private final String growthPeriod;
    private final String waterRequirement;
    private final List<String> reasons;

    public CropRecommendation(String name, String suitability, String expectedYield,
                               String growthPeriod, String waterRequirement, List<String> reasons) {
        this.name = name;
        this.suitability = suitability;
        this.expectedYield = expectedYield;
        this.growthPeriod = growthPeriod;
        this.waterRequirement = waterRequirement;
        this.reasons = reasons;
    }

    public String getName() { return name; }
    public String getSuitability() { return suitability; }
    public String getExpectedYield() { return expectedYield; }
    public String getGrowthPeriod() { return growthPeriod; }
    public String getWaterRequirement() { return waterRequirement; }
    public List<String> getReasons() { return reasons; }
}
