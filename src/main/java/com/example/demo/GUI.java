package com.example.demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.concurrent.Task;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.HashMap;

public class GUI extends Application {

    private Indexing indexing = new Indexing();
    private boolean isIndexed = false;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Search Engine");

        // Show the initial scene with the "Create Index" button
        showIndexCreationScene();
    }

    private void showIndexCreationScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-font-family: 'Arial'; -fx-background-color: white;");

        VBox centerBox = new VBox();
        centerBox.setPadding(new Insets(20));
        centerBox.setSpacing(15);
        centerBox.setStyle("-fx-alignment: center;");

        Button createIndexButton = new Button("Create Index");
        createIndexButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-size: 16px;");
        createIndexButton.setOnAction(event -> createIndex(centerBox));

        centerBox.getChildren().add(createIndexButton);

        root.setCenter(centerBox);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createIndex(VBox centerBox) {
        Label loadingLabel = new Label("Indexing in progress, please wait...");
        loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");
        centerBox.getChildren().setAll(loadingLabel);

        Task<Void> indexingTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Perform the indexing process
                System.out.println("Indexing...");
                indexing.forwardIndexAndLexicon();
                System.out.println("Inverted Indexing...");
                indexing.invertedIndex();
                return null;
            }

            @Override
            protected void succeeded() {
                isIndexed = true;
                // Transition to the search bar scene
                showSearchScene();
            }

            @Override
            protected void failed() {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Indexing Error");
                alert.setHeaderText(null);
                alert.setContentText("An error occurred during the indexing process.");
                alert.showAndWait();
                centerBox.getChildren().setAll(new Button("Create Index"));
            }
        };

        new Thread(indexingTask).start();
    }

    private void showSearchScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-font-family: 'Arial'; -fx-background-color: white;");

        // Top bar for search input
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(20));
        topBar.setSpacing(15);
        topBar.setStyle("-fx-background-color: lightblue; -fx-alignment: center; -fx-border-color: lightgrey; -fx-border-width: 1;");

        TextField searchField = new TextField();
        searchField.setPromptText("Enter search term...");
        searchField.setStyle("-fx-prompt-text-fill: gray; -fx-padding: 10; -fx-font-size: 16px; -fx-border-color: lightgrey; -fx-border-width: 1;");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-size: 16px;");

        // Add key handling for Enter key
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                searchButton.fire();
            }
        });

        topBar.getChildren().addAll(searchField, searchButton);

        // VBox for displaying search results
        VBox resultsBox = new VBox();
        resultsBox.setPadding(new Insets(20));
        resultsBox.setSpacing(15);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(resultsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        searchButton.setOnAction(event -> {
            String searchTerm = searchField.getText();
            resultsBox.getChildren().clear();

            if (!searchTerm.isEmpty()) {
                if (!isIndexed) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Index not created");
                    alert.setHeaderText(null);
                    alert.setContentText("Please create the index before searching.");
                    alert.showAndWait();
                } else {
                    // Perform the actual search
                    performSearch(searchTerm, resultsBox);
                }
            }
        });

        root.setTop(topBar);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
    }

    private void performSearch(String searchTerm, VBox resultsBox) {
        ArrayList<HashMap<String[], Double>> results = indexing.search(searchTerm);

        if (results.isEmpty()) {
            Label noResultsLabel = new Label("No results found.");
            noResultsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");
            resultsBox.getChildren().add(noResultsLabel);
        } else {

            for (int i = 0; i < results.size(); i++) {
                HashMap<String[], Double> result = results.get(i);
                String[] key = result.keySet().iterator().next();
                String title = key[1];
                String url = key[0];
                resultsBox.getChildren().add(createResult(title, url, i));
            }
        }
    }


    private VBox createResult(String title, String url, int index) {
        VBox result = new VBox();
        result.setStyle("-fx-background-color: white; -fx-padding: 10px; -fx-border-radius: 10px; -fx-border-color: lightgrey; -fx-border-width: 1px;");
        result.setOpacity(0);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        Label urlLabel = new Label(url);
        urlLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B46C1;");
        urlLabel.setOnMouseEntered(event -> urlLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B46C1; -fx-underline: true;"));
        urlLabel.setOnMouseExited(event -> urlLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B46C1;"));

        result.getChildren().addAll(titleLabel, urlLabel);

        // Animation
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.2), result);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.setDelay(Duration.seconds(index * 0.05));

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.2), result);
        translateTransition.setFromY(10);
        translateTransition.setToY(0);

        fadeTransition.play();
        translateTransition.play();

        return result;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
