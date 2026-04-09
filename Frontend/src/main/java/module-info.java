module com.smartcrop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    opens com.smartcrop to javafx.fxml, javafx.graphics;
    opens com.smartcrop.view to javafx.fxml, javafx.graphics;
    opens com.smartcrop.model to javafx.base, javafx.fxml;

    exports com.smartcrop;
    exports com.smartcrop.view;
    exports com.smartcrop.model;
}
