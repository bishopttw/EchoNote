package com.example.echonote;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    private Label listen;

    @FXML
    protected void onRecordButton(){listen.setText("Listening......");}

    @FXML
    protected void onStopButton(){listen.setText("Stopped......");}
}