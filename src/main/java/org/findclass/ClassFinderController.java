package org.findclass;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import static org.findclass.Constants.Properties.Last_used_dir;
import static org.findclass.Constants.Properties.Last_used_searchString;
import static org.findclass.SearchProgress.showProgress;

public class ClassFinderController {
    @FXML
    private TextField searchLocation;

    @FXML
    private TextField searchString;

    @FXML
    private CheckBox isRecursive;

    @FXML
    private CheckBox isRegex;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button searchButton;

    @FXML
    private ListView<String> searchResults;

    private Stage stage;

    public void init(Stage stage) {
        this.stage = stage;
        final Properties properties = getLastUsedProperties();
        Object lastUsedLocation = properties.get(Last_used_dir.name());
        if(lastUsedLocation==null)lastUsedLocation=System.getProperty("user.dir");
        searchLocation.setText(lastUsedLocation.toString());
        final Object lastUsedSearchString = properties.get(Last_used_searchString.name());
        if (lastUsedSearchString != null)
            searchString.setText(lastUsedSearchString.toString());
    }

    public void chooseSearchLocation(final ActionEvent actionEvent) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select directory to search");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        final File file = directoryChooser.showDialog(stage);
        if (file != null)
            searchLocation.setText(file.getAbsolutePath());
    }

    private final String lastUsed = System.getProperty("user.dir").concat(File.separator).concat("find-class-fx");


    public Properties getLastUsedProperties() {
        Properties p = new Properties();
        try {
            try (FileInputStream fis = new FileInputStream(lastUsed)) {
                p.load(fis);
                return p;
            }
        } catch (Exception e) {
            System.err.println("user is launching first time perhaps...");
        }
        return p;
    }

    private ShutdownHook lastShutdownHook;

    public void search(ActionEvent actionEvent) {
        if (searchString.getText().isEmpty()) {
            showError("Empty search string!");
            return;
        }
        if (searchLocation.getText().isEmpty()) {
            showError("Where to search?");
            return;
        }
        searchButton.setDisable(true);
        final SearchProgress searchProgress = showProgress(stage, getCancelListener());
        showResults(searchProgress);
        updateLastShutdownHook();
    }

    private void showResults(final SearchProgress searchProgress) {
        try {
            final Collection<String> matches = ClassFinder.searchIn(searchLocation.getText()).find(searchString.getText());
            ObservableList<String> items = FXCollections.observableArrayList(matches);
            //ListView<String> searchResults2=new ListView<>();
            searchResults.setItems(items);
            //scrollPane.setContent(searchResults);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Searching failed!");
        }finally{
            searchProgress.close(null);
        }
    }

    private void updateLastShutdownHook() {
        if (lastShutdownHook != null)
            Runtime.getRuntime().removeShutdownHook(lastShutdownHook);
        lastShutdownHook = new ShutdownHook();
        Runtime.getRuntime().addShutdownHook(lastShutdownHook);
    }

    private EventHandler<Event> getCancelListener() {
        return event -> {
            stage.getScene().getRoot().setEffect(null);
            searchButton.setDisable(false);
        };
    }


    void showError(String message) {
        final Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        final Button ok = new Button("Ok");
        ok.setPadding(new Insets(10, 10, 10, 10));
        ok.setOnAction(actionEvent -> dialogStage.hide());
        final VBox vBox = new VBox(new Text(message),ok);
        vBox.setPadding(new Insets(10,10,10,10));
        vBox.setAlignment(Pos.CENTER);
        dialogStage.setScene(new Scene(vBox,400,200));
        dialogStage.show();
    }

    class ShutdownHook extends Thread {
        public void run() {
            System.out.println("storing last used ...");
            try {
                Properties p = new Properties();
                try (FileOutputStream outputStream = new FileOutputStream(lastUsed)) {
                    p.put(Last_used_dir.name(), searchLocation.getText());
                    p.put(Last_used_searchString.name(), searchString.getText());
                    p.store(outputStream, "Last used properties");
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

        }
    }
}
