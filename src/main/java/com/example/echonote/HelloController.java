package com.example.echonote;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.IOException;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

public class HelloController implements SpeechResultListener {

    @Override
    public void onSpeechRecognized(String text){
        listen.setText("Processing....");
        listen.setStyle("-fx-font-size:24px; -fx-text-fill: #f39c12; -fx-font-weight: bold;");

//        PROCESS THE RECOGNIZED TEXT AS A COMMAND
        String response = CommandProcessor.processCommand(text);

        listen.setText(response);
        listen.setStyle("-fx-font-size: 20px; -fx-text-fill: #2ecc71; -fx-font-weight: bold;");

//        RESET AFTER 3 SECONDS
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> {
            listen.setText("Ready to listen!");
            listen.setStyle("-fx-font-size:24px; -fx-text-fill: white; -fx-font-weight: bold;");

            // Reset button
            recordButton.setText("RECORD");
            recordButton.setStyle("-fx-background-color: #4a90e2; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-radius: 30;");

            // Reset microphone
            animateMicrophone(false);

            // Reset listening flag
            isListening = false;
        });
        pause.play();
    }

    @FXML
    private FontIcon micIcon;

    @FXML
    private Button recordButton;

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    private Label listen;

    @FXML
    protected void onRecordButton(){
        if (!isListening){
//            START LISTENING
            listen.setText("🎤 Listening... (5 seconds)");
            listen.setStyle("-fx-font-size: 24px; -fx-text-fill: #ff6b6b; -fx-font-weight: bold;");

//            ANIMATE MICROPHONE -PULSE EFFECT
            animateMicrophone(true);

//            CHANGE BUTTON APPEARANCE
            recordButton.setText("LISTENING.....");
            recordButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-radius: 30;");

            recognizer.startListening();
            isListening = true;
        }else {
//            STOP LISTENING
            listen.setText("Ready to listen!");
            listen.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");

            // Stop animation
            animateMicrophone(false);

            // Reset button
            recordButton.setText("RECORD");
            recordButton.setStyle("-fx-background-color: #4a90e2; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-radius: 30;");

            recognizer.stopListening();
            isListening = false;
        }
    }

    private ScaleTransition micAnimation;

    private void animateMicrophone(boolean start) {
        if (start) {
            // Create pulsing animation
            micAnimation = new ScaleTransition(Duration.seconds(0.8), micIcon);
            micAnimation.setFromX(1.0);
            micAnimation.setFromY(1.0);
            micAnimation.setToX(1.3);
            micAnimation.setToY(1.3);
            micAnimation.setCycleCount(ScaleTransition.INDEFINITE);
            micAnimation.setAutoReverse(true);
            micAnimation.play();

            // Change mic color to red
            micIcon.setIconColor(javafx.scene.paint.Color.web("#ff6b6b"));
        } else {
            // Stop animation
            if (micAnimation != null) {
                micAnimation.stop();
            }
            micIcon.setScaleX(1.0);
            micIcon.setScaleY(1.0);

            // Reset mic color to blue
            micIcon.setIconColor(javafx.scene.paint.Color.web("#4a90e2"));
        }
    }


//   SPEECH RECOGNITION USING ASSEMBLY AI
    private  AssemblyAISpeechRecognizer recognizer;
    private boolean isListening = false;

    @FXML
    public void initialize(){
        try{
            listen.setText("Ready! Click Record");
            recognizer = new AssemblyAISpeechRecognizer();
            recognizer.setResultListener(this);

        } catch (Exception e) {
            listen.setText("Using simple mode");
            e.printStackTrace();
        }
    }
}