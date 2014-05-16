package org.findclass;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MessageDialog {
    private final String message;

    public MessageDialog(final String message) {
        this.message = message;
    }

    public void show(final Stage ownerStage) {
        final Stage dialog = new Stage(StageStyle.TRANSPARENT);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(ownerStage);
        dialog.setScene(buildScene(ownerStage, dialog));
        dialog.getScene().getStylesheets().add(getClass().getResource("/message.css").toExternalForm());
        final Node root = dialog.getScene().getRoot();
        final Coordinates dragCoordinates = new Coordinates();
        root.setOnMousePressed(mouseEvent -> {
            dragCoordinates.x = dialog.getX() - mouseEvent.getScreenX();
            dragCoordinates.y = dialog.getY() - mouseEvent.getScreenY();
        });
        root.setOnMouseDragged(mouseEvent -> {
            dialog.setX(mouseEvent.getScreenX() + dragCoordinates.x);
            dialog.setY(mouseEvent.getScreenY() + dragCoordinates.y);
        });
        ownerStage.getScene().getRoot().setEffect(new BoxBlur());
        dialog.show();
    }

    private Scene buildScene(Stage stage, Stage dialog) {
        final Button okButton = new Button("OK");
        okButton.cancelButtonProperty().set(true);
        okButton.setOnAction(closeDialog(stage, dialog));
        final HBox hbox = new HBox(new Label(message), okButton);
        hbox.getStyleClass().add("message-dialog");
        return new Scene(hbox, Color.TRANSPARENT);
    }

    private EventHandler<ActionEvent> closeDialog(Stage stage, Stage dialog) {
        return actionEvent -> {
            stage.getScene().getRoot().setEffect(null);
            dialog.close();
        };
    }

    public static void error(final String s, final Stage stage) {
        new MessageDialog(s).show(stage);
    }

    class Coordinates {
        double x, y;
    }
}
