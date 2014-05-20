package org.findclass;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import static org.findclass.ClassFinder.searchIn;
import static org.findclass.Constants.LAST_USED_FILE;
import static org.findclass.Constants.Properties.*;
import static org.findclass.ErrorDialog.showError;
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
    private Button searchButton;

    @FXML
    private ListView<String> searchResults;

    @FXML
    private Label totalHits;

    private Stage stage;

    public void init(Stage stage) {
        this.stage = stage;
        final Properties properties = getLastUsedProperties();
        Object lastUsedLocation = properties.get(Last_used_dir.name());
        Object lastUsedRegex = properties.get(Last_used_isRegex.name());
        Object lastUsedRecursive = properties.get(Last_used_isRecursive.name());
        if (lastUsedLocation == null) lastUsedLocation = System.getProperty("user.dir");
        searchLocation.setText(lastUsedLocation.toString());
        final Object lastUsedSearchString = properties.get(Last_used_searchString.name());
        if (lastUsedSearchString != null)
            searchString.setText(lastUsedSearchString.toString());
        if (lastUsedRegex != null)
            isRegex.setSelected(Boolean.valueOf(lastUsedRegex.toString().trim()));
        if (lastUsedRecursive != null)
            isRecursive.setSelected(Boolean.valueOf(lastUsedRecursive.toString().trim()));
    }

    public void search(final ActionEvent actionEvent) {
        if (searchString.getText().isEmpty()) {
            showError("Empty search string!", stage);
            return;
        }
        if (searchLocation.getText().isEmpty()) {
            showError("Where to search?", stage);
            return;
        }
        searchButton.setDisable(true);
        final SearchProgress searchProgress = showProgress(stage, getCancelListener());
        final Thread t = new Thread(() -> showResults(searchProgress, actionEvent));
        t.setDaemon(true);
        t.start();
        updateLastShutdownHook();
    }

    private void showResults(final SearchProgress searchProgress, ActionEvent actionEvent) {
        try {
            searchIn(searchLocation.getText())
                    .recursive(isRecursive.isSelected())
                    .regex(isRegex.isSelected())
                    .collectMatches(searchString.getText(), getMatchListener());

            /*final Collection<String> matches = searchIn(searchLocation.getText())
                    .recursive(isRecursive.isSelected())
                    .regex(isRegex.isSelected())
                    .find(searchString.getText());
            Platform.runLater(() -> {
                ObservableList<String> items = FXCollections.observableArrayList(matches);
                String text="Hits: "+matches.size();
                if (matches.size() == 0) {
                    text="Nothing was found!";
                }
                totalHits.setText(text);
                searchResults.setItems(items);
            });*/
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                searchProgress.close(actionEvent);
                showError(e.getMessage(), stage);
            });
            return;
        }
        Platform.runLater(() -> searchProgress.close(null));
    }

    private EventHandler<Event> getCancelListener() {
        return event -> {
            stage.getScene().getRoot().setEffect(null);
            searchButton.setDisable(false);
        };
    }

    private final Object lock = new Object();

    private MatchListener getMatchListener() {
        return jarFile -> Platform.runLater(() -> {
            try {
                //synchronized (lock) {
                    if (!searchResults.getItems().contains(jarFile.getName())) {
                        searchResults.getItems().add(jarFile.getName());
                        final int resultSize = searchResults.getItems().size();
                        String text = "Hits: " + resultSize;
                        if (resultSize == 0) {
                            text = "Nothing was found!";
                        }
                        totalHits.setText(text);
                    }
                //}
            } catch (Exception e) {
                e.printStackTrace();
                showError(e.getMessage(), stage);
            }
        });
    }

    public void chooseSearchLocation(final ActionEvent e) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select directory to search");
        String initialLocation = searchLocation.getText();
        if (initialLocation == null || initialLocation.length() < 1 || !Files.exists(Paths.get(initialLocation)))
            initialLocation = System.getProperty("user.home");
        directoryChooser.setInitialDirectory(new File(initialLocation));
        final File file = directoryChooser.showDialog(stage);
        if (file != null)
            searchLocation.setText(file.getAbsolutePath());
    }

    public Properties getLastUsedProperties() {
        final Properties p = new Properties();
        try {
            try (final FileInputStream fis = new FileInputStream(LAST_USED_FILE)) {
                p.load(fis);
                return p;
            }
        } catch (Exception e) {
            System.err.println("user is launching first time perhaps...");
        }
        return p;
    }


    private ShutdownHook lastShutdownHook;

    private void updateLastShutdownHook() {
        if (lastShutdownHook != null)
            Runtime.getRuntime().removeShutdownHook(lastShutdownHook);
        lastShutdownHook = new ShutdownHook();
        Runtime.getRuntime().addShutdownHook(lastShutdownHook);
    }

    class ShutdownHook extends Thread {
        public void run() {
            try {
                final Properties p = new Properties();
                try (FileOutputStream outputStream = new FileOutputStream(LAST_USED_FILE)) {
                    p.put(Last_used_dir.name(), searchLocation.getText());
                    p.put(Last_used_searchString.name(), searchString.getText());
                    p.put(Last_used_isRegex.name(), "" + isRegex.isSelected());
                    p.put(Last_used_isRecursive.name(), "" + isRecursive.isSelected());
                    p.store(outputStream, "Last used properties");
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

        }
    }
}
