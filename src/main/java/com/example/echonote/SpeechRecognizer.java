package com.example.echonote;

import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.*;
import java.io.IOException;

public class SpeechRecognizer {

    private Model model;
    private Recognizer recognizer;
    private TargetDataLine microphone;
    private boolean isListening = false;

    public SpeechRecognizer (String modelPath) throws IOException{
        this.model = new Model(modelPath);
        this.recognizer = new Recognizer(model, 16000);
    }

    public void startListening(){
        isListening = true;

        new Thread(()->{
            try{
                AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                microphone = (TargetDataLine) AudioSystem.getLine(info);
                microphone.open(format);
                microphone.start();

                byte[] buffer = new byte[4096];

                while(isListening){
                    int bytesRead = microphone.read(buffer, 0, buffer.length);

                    if(recognizer.acceptWaveForm(buffer, bytesRead)){
                        String result = recognizer.getResult();
                        System.out.println(("Recognized: " + result));
                    }
                }
            }catch (LineUnavailableException e){
                e.printStackTrace();
            }
        }).start();
    }

    public  void stopListening(){
        isListening = false;
        if(microphone != null){
            microphone.stop();
            microphone.close();
        }
    }
}
