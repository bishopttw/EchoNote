package com.example.echonote;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.io.IOException;

public class HelloController implements SpeechResultListener {

    @Override
    public void onSpeechRecognized(String text){
//        PROCESS THE RECOGNIZED TEXT AS A COMMAND
        String response = CommandProcessor.processCommand(text);
        listen.setText(response);
    }

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
            listen.setText("Recording.....(5 secs)");
            recognizer.startListening();
            isListening = true;
        }else {
//            STOP LISTENING
            listen.setText("Ready to listen!");
            recognizer.stopListening();
            isListening = false;
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