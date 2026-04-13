package com.smartcrop.service;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.awt.Desktop;
import java.io.File;

public class PdfService {

    public static String generateReportPdf(
            String title,
            JsonObject soil,
            JsonObject weather,
            JsonArray recommendations) throws Exception {

        String filePath = "report_" + System.currentTimeMillis() + ".pdf";

        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // 🟢 TITLE
        document.add(new Paragraph(title)
                .setBold()
                .setFontSize(20));

        document.add(new Paragraph("\n"));

        // 🌱 SOIL DATA
        document.add(new Paragraph("Soil Data").setBold());

        if (soil != null) {
            for (String key : soil.keySet()) {
                JsonElement value = soil.get(key);
                document.add(new Paragraph(key + ": " + value.toString()));
            }
        } else {
            document.add(new Paragraph("No soil data available"));
        }

        document.add(new Paragraph("\n"));

        // 🌦 WEATHER DATA
        document.add(new Paragraph("Weather Data").setBold());

        try {
            JsonObject main = weather.getAsJsonObject("main");

            document.add(new Paragraph("Temperature: " + main.get("temp").getAsDouble() + " °C"));
            document.add(new Paragraph("Humidity: " + main.get("humidity").getAsDouble() + " %"));

        } catch (Exception e) {
            document.add(new Paragraph("Weather data not available"));
        }

        document.add(new Paragraph("\n"));

        // 🌾 RECOMMENDATIONS
        document.add(new Paragraph("Crop Recommendations").setBold());

        if (recommendations != null && recommendations.size() > 0) {
            for (int i = 0; i < recommendations.size(); i++) {
                JsonObject crop = recommendations.get(i).getAsJsonObject();

                String name = crop.has("name") ? crop.get("name").getAsString() : "Unknown";
                String suitability = crop.has("suitability") ? crop.get("suitability").getAsString() : "N/A";

                document.add(new Paragraph("• " + name + " (" + suitability + ")"));
            }
        } else {
            document.add(new Paragraph("No recommendations available"));
        }

        document.close();
        // 🔥 AUTO OPEN PDF
        File file = new File(filePath);

        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file);
        }

        return filePath;
    }
}