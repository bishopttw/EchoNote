package com.example.echonote;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.sound.sampled.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * Speech Recognition using AssemblyAI Cloud API
 * Records audio from microphone, uploads to AssemblyAI,
 * and returns transcribed text.
 */

public class AssemblyAISpeechRecognizer {

    private static final String API_KEY = "f011014fe19e4ccf874a9ffbfdc65805";
    private static final String UPLOAD_URL = "https://api.assemblyai.com/v2/upload";
    private static final String TRANSCRIPT_URL = "https://api.assemblyai.com/v2/transcript";

    private SpeechResultListener listener;
    private TargetDataLine microphone;
    private boolean isListening = false;
    private OkHttpClient client;

    public AssemblyAISpeechRecognizer() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public void setResultListener(SpeechResultListener listener) {
        this.listener = listener;
    }

    public void startListening() {
        isListening = true;

        new Thread(() -> {
            try {
                // Record 5 seconds of audio
                byte[] audioData = recordAudio(5);

                if (audioData != null && audioData.length > 0) {
                    // Send to AssemblyAI
                    recognizeAudio(audioData);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isListening = false;
            }
        }).start();
    }

    private byte[] recordAudio(int seconds) {
        try {
            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];

            long endTime = System.currentTimeMillis() + (seconds * 1000);

            while (System.currentTimeMillis() < endTime && isListening) {
                int bytesRead = microphone.read(buffer, 0, buffer.length);
                out.write(buffer, 0, bytesRead);
            }

            microphone.stop();
            microphone.close();

            return out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void recognizeAudio(byte[] audioData) {
        try {
            // Step 1: Save audio as proper WAV file
            File wavFile = new File("recording.wav");
            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);

            ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
            AudioInputStream audioInputStream = new AudioInputStream(
                    bais,
                    format,
                    audioData.length / format.getFrameSize()
            );

            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, wavFile);
            System.out.println("WAV file saved: " + wavFile.getAbsolutePath());

            // Step 2: Upload the WAV FILE (not raw bytes!)
            RequestBody uploadBody = RequestBody.create(
                    MediaType.parse("audio/wav"),
                    wavFile
            );

            Request uploadRequest = new Request.Builder()
                    .url(UPLOAD_URL)
                    .addHeader("authorization", API_KEY)
                    .post(uploadBody)
                    .build();

            Response uploadResponse = client.newCall(uploadRequest).execute();
            String uploadJson = uploadResponse.body().string();
            JSONObject uploadResult = new JSONObject(uploadJson);
            String uploadUrl = uploadResult.getString("upload_url");

            System.out.println("Audio uploaded: " + uploadUrl);

            // Step 3: Request transcription
            JSONObject transcriptRequest = new JSONObject();
            transcriptRequest.put("audio_url", uploadUrl);

            JSONArray modelsArray = new JSONArray();
            modelsArray.put("universal-2");
            transcriptRequest.put("speech_models", modelsArray);

            transcriptRequest.put("language_code", "en");

            System.out.println("DEBUG - Request JSON: " + transcriptRequest.toString());

            RequestBody transcriptBody = RequestBody.create(
                    MediaType.parse("application/json"),
                    transcriptRequest.toString()
            );

            Request transcriptReq = new Request.Builder()
                    .url(TRANSCRIPT_URL)
                    .addHeader("authorization", API_KEY)
                    .addHeader("content-type", "application/json")
                    .post(transcriptBody)
                    .build();

            Response transcriptResponse = client.newCall(transcriptReq).execute();
            String transcriptJson = transcriptResponse.body().string();

            System.out.println("Transcript Response: " + transcriptJson);
            JSONObject transcriptResult = new JSONObject(transcriptJson);

            if(!transcriptResult.has("id")){
                System.err.println("ERROR: Response doesn't have 'id' field!");
                System.err.println("Full response: " + transcriptJson);
                return;
            }

            String transcriptId = transcriptResult.getString("id");
            System.out.println("Transcription ID: " + transcriptId);

            // Step 4: Poll for result
            String text = pollForResult(transcriptId);

            if (text != null && listener != null) {
                javafx.application.Platform.runLater(() -> {
                    listener.onSpeechRecognized(text);
                });
            }

            // Clean up temp file
            wavFile.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String pollForResult(String transcriptId) {
        try {
            String pollingUrl = TRANSCRIPT_URL + "/" + transcriptId;

            for (int i = 0; i < 30; i++) {  // Poll for up to 30 seconds
                Request request = new Request.Builder()
                        .url(pollingUrl)
                        .addHeader("authorization", API_KEY)
                        .get()
                        .build();

                Response response = client.newCall(request).execute();
                String json = response.body().string();
                JSONObject result = new JSONObject(json);

                String status = result.getString("status");
                System.out.println("Status: " + status);

                if (status.equals("completed")) {
                    return result.getString("text");
                } else if (status.equals("error")) {
                    System.err.println("Transcription error");
                    System.err.println("Full error response: " + json);

                    if (result.has("error")){
                        System.err.println("Error message: " + result.getString("error"));
                    }
                    return null;
                }

                Thread.sleep(1000);  // Wait 1 second before polling again
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void stopListening() {
        isListening = false;
        if (microphone != null) {
            microphone.stop();
            microphone.close();
        }
    }
}
