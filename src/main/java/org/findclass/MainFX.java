package org.findclass;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

import static org.findclass.ClassFinder.loadResource;

public class MainFX extends Application {

    private final Group root = new Group();

    private void init(final Stage primaryStage) throws IOException {
        final FXMLLoader fxmlLoader = new FXMLLoader(loadResource("./main.fxml"));
        final BorderPane contentPane = fxmlLoader.load();
        final ClassFinderController controller = fxmlLoader.getController();
        controller.init(primaryStage);
        primaryStage.setTitle("Find class");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root));
        root.getChildren().add(contentPane);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        init(primaryStage);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }


}
