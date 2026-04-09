package com.smartcrop.view;

import com.smartcrop.model.SoilData;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.function.Consumer;

public class SoilDataView {

    private final VBox root;

    public SoilDataView(Consumer<SoilData> onSave) {
        root = new VBox(20);
        root.getStyleClass().add("card");

        Label title = new Label("Soil Data Entry");
        title.getStyleClass().add("card-title");

        // Soil type
        VBox soilTypeBox = new VBox(6);
        Label soilTypeLabel = new Label("Soil Type");
        soilTypeLabel.getStyleClass().add("form-label");
        ComboBox<String> soilTypeCombo = new ComboBox<>(
                FXCollections.observableArrayList("clay", "sandy", "loamy", "silty", "peaty", "chalky")
        );
        soilTypeCombo.setValue("loamy");
        soilTypeCombo.setMaxWidth(Double.MAX_VALUE);
        soilTypeCombo.getStyleClass().add("form-combo");
        soilTypeBox.getChildren().addAll(soilTypeLabel, soilTypeCombo);

        // pH and Moisture row
        GridPane phMoistureGrid = new GridPane();
        phMoistureGrid.setHgap(16);
        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        phMoistureGrid.getColumnConstraints().addAll(col1, col2);

        Spinner<Double> phSpinner = makeDoubleSpinner(0, 14, 7.0, 0.1);
        Spinner<Double> moistureSpinner = makeDoubleSpinner(0, 100, 25.0, 1.0);

        phMoistureGrid.add(buildFieldBox("pH Level", "Range: 0-14", phSpinner), 0, 0);
        phMoistureGrid.add(buildFieldBox("Moisture (%)", "Range: 0-100%", moistureSpinner), 1, 0);

        // Nutrient levels
        Label nutrientHeader = new Label("Nutrient Levels (mg/kg)");
        nutrientHeader.getStyleClass().add("section-label");

        GridPane nutrientGrid = new GridPane();
        nutrientGrid.setHgap(12);
        ColumnConstraints nc1 = new ColumnConstraints(); nc1.setPercentWidth(33.3);
        ColumnConstraints nc2 = new ColumnConstraints(); nc2.setPercentWidth(33.3);
        ColumnConstraints nc3 = new ColumnConstraints(); nc3.setPercentWidth(33.3);
        nutrientGrid.getColumnConstraints().addAll(nc1, nc2, nc3);

        Spinner<Double> nSpinner = makeDoubleSpinner(0, 500, 50.0, 1.0);
        Spinner<Double> pSpinner = makeDoubleSpinner(0, 500, 30.0, 1.0);
        Spinner<Double> kSpinner = makeDoubleSpinner(0, 500, 40.0, 1.0);

        nutrientGrid.add(buildFieldBox("Nitrogen (N)", "", nSpinner), 0, 0);
        nutrientGrid.add(buildFieldBox("Phosphorus (P)", "", pSpinner), 1, 0);
        nutrientGrid.add(buildFieldBox("Potassium (K)", "", kSpinner), 2, 0);

        // Organic Matter
        Spinner<Double> organicSpinner = makeDoubleSpinner(0, 100, 3.5, 0.1);
        VBox organicBox = buildFieldBox("Organic Matter (%)", "Typical range: 1-10%", organicSpinner);

        // Save button
        Button saveBtn = new Button("💾  Save Soil Data");
        saveBtn.getStyleClass().add("btn-primary");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> {
            SoilData data = new SoilData(
                    soilTypeCombo.getValue(),
                    phSpinner.getValue(),
                    nSpinner.getValue(),
                    pSpinner.getValue(),
                    kSpinner.getValue(),
                    organicSpinner.getValue(),
                    moistureSpinner.getValue()
            );
            onSave.accept(data);
        });

        root.getChildren().addAll(title, soilTypeBox, phMoistureGrid,
                nutrientHeader, nutrientGrid, organicBox, saveBtn);
    }

    private Spinner<Double> makeDoubleSpinner(double min, double max, double init, double step) {
        SpinnerValueFactory.DoubleSpinnerValueFactory factory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, init, step);
        Spinner<Double> spinner = new Spinner<>(factory);
        spinner.setEditable(true);
        spinner.setMaxWidth(Double.MAX_VALUE);
        spinner.getStyleClass().add("form-spinner");
        return spinner;
    }

    private VBox buildFieldBox(String label, String hint, Control control) {
        VBox box = new VBox(5);
        Label lbl = new Label(label);
        lbl.getStyleClass().add("form-label");
        box.getChildren().addAll(lbl, control);
        if (!hint.isEmpty()) {
            Label hintLabel = new Label(hint);
            hintLabel.getStyleClass().add("muted-text");
            hintLabel.setStyle("-fx-font-size: 11px;");
            box.getChildren().add(hintLabel);
        }
        return box;
    }

    public VBox getRoot() { return root; }
}
