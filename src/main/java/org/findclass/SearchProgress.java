package org.findclass;

import javafx.event.ActionEvent;
import javafx.event.Event;
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
    private EventHandler<Event> cancelListener;

    public static SearchProgress showProgress(final Stage ownerStage,final EventHandler<Event> cancelListener) {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(loadResource("./progress.fxml"));
            final Pane contentPane = fxmlLoader.load();
            final SearchProgress searchProgress = fxmlLoader.getController();
            searchProgress.init(ownerStage, contentPane);
            searchProgress.cancelListener=cancelListener;
            return searchProgress;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void init(final Stage ownerStage, final Pane contentPane) {
        this.myStage = new Stage(StageStyle.TRANSPARENT);
        myStage.setScene(new Scene(contentPane, Color.TRANSPARENT));
        myStage.initModality(Modality.WINDOW_MODAL);
        myStage.initOwner(ownerStage);
        ownerStage.getScene().getRoot().setEffect(new BoxBlur());
        myStage.show();
    }

    public void close(ActionEvent actionEvent) {
        myStage.close();
        cancelListener.handle(actionEvent);
    }
}
