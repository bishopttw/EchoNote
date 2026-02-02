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
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("splash-view.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);
        stage.setTitle("EchoNote");
        stage.getIcons().add(new Image(
                Objects.requireNonNull(
                        HelloApplication.class.getResourceAsStream("/icons/app-icon.png")
                )
        )
        );

        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}