package com.smartcrop.model;

import java.util.List;

public class WeatherData {
    private final double temperature;
    private final double humidity;
    private final double rainfall;
    private final double windSpeed;
    private final String condition;
    private final List<ForecastDay> forecast;

    public WeatherData(double temperature, double humidity, double rainfall,
                       double windSpeed, String condition, List<ForecastDay> forecast) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.rainfall = rainfall;
        this.windSpeed = windSpeed;
        this.condition = condition;
        this.forecast = forecast;
    }

    public double getTemperature() { return temperature; }
    public double getHumidity() { return humidity; }
    public double getRainfall() { return rainfall; }
    public double getWindSpeed() { return windSpeed; }
    public String getCondition() { return condition; }
    public List<ForecastDay> getForecast() { return forecast; }

    public static class ForecastDay {
        private final String day;
        private final double temp;
        private final String condition;

        public ForecastDay(String day, double temp, String condition) {
            this.day = day;
            this.temp = temp;
            this.condition = condition;
        }

        public String getDay() { return day; }
        public double getTemp() { return temp; }
        public String getCondition() { return condition; }
    }
}
