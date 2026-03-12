package com.example.echonote;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import javafx.animation.TranslateTransition;

public class SplashController {
//    DEPENDENCY INJECTION
    @FXML
    private ImageView waveImage;

    @FXML
    private VBox waveSection;

    @FXML
    private VBox welcomeSection;

    @FXML
    private ImageView micImage;

    @FXML
    private Label appNameLabel;

    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize(){
        animateWave();
    }
// ANIMATION METHOD
    private void animateWave(){

        TranslateTransition wave1 = new TranslateTransition(Duration.seconds(0.4), waveImage);
        wave1.setFromY(0);
        wave1.setToY(-15);

        TranslateTransition wave2 = new TranslateTransition(Duration.seconds(0.4), waveImage);
        wave2.setFromY(-15);
        wave2.setToY(0);

        TranslateTransition wave3 = new TranslateTransition(Duration.seconds(0.4), waveImage);
        wave3.setFromY(0);
        wave3.setToY(15);

        TranslateTransition wave4 = new TranslateTransition(Duration.seconds(0.4), waveImage);
        wave4.setFromY(15);
        wave4.setToY(0);

        SequentialTransition fade = new SequentialTransition(wave1, wave2, wave3, wave4);
        fade.setCycleCount(3);

        fade.setOnFinished(event -> showWelcomeScreen());

        fade.play();
    }

// SHOW WELCOME SCREEN METHOD
    private void showWelcomeScreen(){
        waveSection.setVisible(false);
        welcomeSection.setVisible(true);

        playWelcomeVoice();
        PauseTransition pause = new PauseTransition(Duration.seconds(4));
        pause.setOnFinished(event -> openMainApp());
        pause.play();
    }

//    PLAY WELCOME VOICE METHOD
    private void playWelcomeVoice(){
        try{
            String command = "powershell -Command \"Add-Type -AssemblyName System.Speech; "+
                             "$speak = New-Object System.Speech.Synthesis.SpeechSynthesizer; " +
                             "$speak.Speak('Welcome to EchoNote');\"";
            new Thread(() -> {
                try{
                    Runtime.getRuntime().exec(command);
                }catch (IOException e){
                    System.out.println("Could not play voice: " + e.getMessage());
                }
            }).start();
        } catch (Exception e) {
            System.out.println("Error in playWelcomeVoice: "+ e.getMessage());
        }
    }

//    OPEN MAIN APP
    private void openMainApp(){
        try{
            Stage splashStage = (Stage) waveImage.getScene().getWindow();

            splashStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Scene mainScene = new Scene(loader.load(), 800, 600);

            Stage mainStage = new Stage();
            mainStage.setTitle("EchoNote - Voice Command App");
            mainStage.setScene(mainScene);
            mainStage.centerOnScreen();
            mainStage.show();
        } catch (IOException e) {
            System.out.println("Error opening main app: "+ e.getMessage());
            e.printStackTrace();
        }
    }
}
