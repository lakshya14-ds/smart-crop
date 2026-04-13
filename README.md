# 🌱 Smart Crop Advisory System -JavaFx

A modern **Crop Recommendation & Advisory System** built using JavaFX and Supabase.  
This application helps farmers and users make **data-driven decisions** based on soil conditions and real-time weather.

---

## 🚀 Features

### 🌾 Crop Recommendations
- Intelligent crop suggestions based on:
  - Soil data (pH, nutrients, moisture, etc.)
  - Real-time weather data
- Multi-level recommendations:
  - 🟢 Excellent Match
  - 🔵 Good Match
  - 🟡 Fair Match

---

### 🌦 Real-Time Weather Integration
- Fetches live weather data using API
- Displays:
  - Temperature 🌡
  - Humidity 💧
  - Rainfall 🌧
  - Wind Speed 💨
- 5-day forecast support (optional/extendable)

---

### 🌱 Soil Data Management
- Input and store soil parameters:
  - Soil type
  - pH level
  - Nitrogen, Phosphorus, Potassium
  - Organic matter
  - Moisture
- Stored securely in Supabase database

---

### 📊 Smart Advisory System
- 💧 Irrigation suggestions
- 🌿 Fertilizer recommendations
- 📈 Condition analysis (soil + weather)

---

### 📄 Report Generation
- Generate **Comprehensive Reports**
- Includes:
  - Soil data
  - Weather data
  - Crop recommendations
- Export as **PDF**
- Stored and retrievable from database

---

### 🎨 Modern UI/UX
- Built using JavaFX
- Clean dashboard layout
- Card-based design
- Responsive and user-friendly interface

---

## 🛠 Tech Stack

### 💻 Frontend
- JavaFX
- CSS (Custom styling)

### ⚙️ Backend
- Java (Core + OOP)
- REST API Integration

### ☁️ Database
- Supabase (PostgreSQL)
- RESTful data operations

### 🌐 APIs
- Weather API (OpenWeatherMap)

### 📄 PDF Generation
- iText PDF Library

### 🔧 Build Tool
- Maven

---


## Requirements
- Java 17 or newer (Java 21 recommended)
- Maven 3.8+
- JavaFX is bundled via Maven — no separate install needed

---

## Project Structure

```
SmartCropAdvisory/
├── pom.xml
└── src/main/
    ├── java/
    │   ├── module-info.java
    │   └── com/smartcrop/
    │       ├── Launcher.java          ← fat-JAR entry point
    │       ├── MainApp.java           ← JavaFX Application
    │       ├── DashboardApp.java      ← Dashboard orchestrator
    │       ├── model/
    │       │   ├── SoilData.java
    │       │   ├── WeatherData.java
    │       │   └── CropRecommendation.java
    │       └── view/
    │           ├── LoginView.java
    │           ├── WeatherView.java
    │           ├── SoilDataView.java
    │           ├── CropRecommendationsView.java
    │           ├── IrrigationView.java
    │           ├── FertilizerView.java
    │           └── ReportsView.java
    └── resources/
        └── styles/
            └── app.css
```

---

## Build & Run

### Option 1 — Run directly with Maven (easiest)
```bash
cd SmartCropAdvisory
mvn clean javafx:run
```

### Option 2 — Build a fat JAR and run
```bash
cd SmartCropAdvisory
mvn clean package

# The fat JAR is in target/
java -jar target/smart-crop-advisory-1.0.0-fat.jar
```

### Option 3 — IDE (IntelliJ IDEA / Eclipse)
1. Open the project as a Maven project.
2. Mark `src/main/java` as Sources Root.
3. Mark `src/main/resources` as Resources Root.
4. Run `com.smartcrop.MainApp` as the main class.
   - In IntelliJ: Add VM options `--add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED` if needed.

---

## Features Implemented

| React Screen           | JavaFX Equivalent            |
|------------------------|------------------------------|
| Login / Register       | `LoginView`                  |
| Dashboard + Tab Nav    | `DashboardApp`               |
| Weather Display        | `WeatherView`                |
| Soil Data Entry        | `SoilDataView`               |
| Crop Recommendations   | `CropRecommendationsView`    |
| Irrigation Advice      | `IrrigationView`             |
| Fertilizer Advice      | `FertilizerView`             |
| Report Generation      | `ReportsView`                |

All tabs: Overview, Soil Data, Weather, Recommendations, Reports

---

## Notes
- Demo mode: enter any email to log in (same as original React app).
- Soil data changes on the Soil Data tab are reflected live across Overview and Recommendations tabs.
- Styled with a custom `app.css` matching the original green/white theme.
