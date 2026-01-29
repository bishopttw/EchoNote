package com.example.echonote;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader Loader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(Loader.load(), 900, 600);
        stage.setTitle("EchoNote");
        stage.getIcons().add(new Image(
                Objects.requireNonNull(
                        HelloApplication.class.getResourceAsStream("/icons/app-icon.png")
                )
        )
        );

        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}