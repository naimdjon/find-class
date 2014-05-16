package org.findclass;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import static org.findclass.ClassFinder.loadResource;

public class SearchProgress {

    //private Stage ownerStage;

    private Stage myStage;
    private EventHandler cancelListener;

    public static void showProgress(final Stage ownerStage,final EventHandler cancelListener) {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(loadResource("./progress.fxml"));
            final Pane contentPane = fxmlLoader.load();
            final SearchProgress controller = fxmlLoader.getController();
            controller.init(ownerStage, contentPane);
            controller.cancelListener=cancelListener;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init(final Stage ownerStage, final Pane contentPane) {
        this.myStage = new Stage(StageStyle.TRANSPARENT);
        myStage.setScene(new Scene(contentPane, Color.TRANSPARENT));
        myStage.initModality(Modality.WINDOW_MODAL);
        myStage.initOwner(ownerStage);
        ownerStage.getScene().getRoot().setEffect(new BoxBlur());
        myStage.show();
    }

    public void cancel(ActionEvent actionEvent) {
        myStage.close();
        cancelListener.handle(actionEvent);
    }
}
