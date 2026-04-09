package com.smartcrop.model;

public class SoilData {
    private String soilType;
    private double ph;
    private double nitrogen;
    private double phosphorus;
    private double potassium;
    private double organicMatter;
    private double moisture;

    public SoilData(String soilType, double ph, double nitrogen, double phosphorus,
                    double potassium, double organicMatter, double moisture) {
        this.soilType = soilType;
        this.ph = ph;
        this.nitrogen = nitrogen;
        this.phosphorus = phosphorus;
        this.potassium = potassium;
        this.organicMatter = organicMatter;
        this.moisture = moisture;
    }

    public String getSoilType() { return soilType; }
    public double getPh() { return ph; }
    public double getNitrogen() { return nitrogen; }
    public double getPhosphorus() { return phosphorus; }
    public double getPotassium() { return potassium; }
    public double getOrganicMatter() { return organicMatter; }
    public double getMoisture() { return moisture; }

    public void setSoilType(String soilType) { this.soilType = soilType; }
    public void setPh(double ph) { this.ph = ph; }
    public void setNitrogen(double nitrogen) { this.nitrogen = nitrogen; }
    public void setPhosphorus(double phosphorus) { this.phosphorus = phosphorus; }
    public void setPotassium(double potassium) { this.potassium = potassium; }
    public void setOrganicMatter(double organicMatter) { this.organicMatter = organicMatter; }
    public void setMoisture(double moisture) { this.moisture = moisture; }
}
