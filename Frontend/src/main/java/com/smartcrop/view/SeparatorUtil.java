package com.smartcrop.view;

import javafx.scene.control.Separator;

// Re-export for convenience (already in javafx.scene.control)
// This is just a placeholder - use javafx.scene.control.Separator directly
public class SeparatorUtil {
    public static Separator horizontal() {
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #e5e7eb; -fx-pref-height: 1px;");
        return sep;
    }
}
