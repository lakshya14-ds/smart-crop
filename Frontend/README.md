# Smart Crop Advisory System — JavaFX

A full JavaFX conversion of the React/TypeScript Smart Crop Advisory frontend.

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
