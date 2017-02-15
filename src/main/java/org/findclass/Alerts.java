package org.findclass;


import javafx.scene.control.Alert;

class Alerts {

    static void showError(final String errorMessage) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("");
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

}
