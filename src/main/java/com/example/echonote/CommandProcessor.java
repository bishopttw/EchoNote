package com.example.echonote;

import java.io.IOException;

public class CommandProcessor {

    public static String processCommand(String recognizedText) {
        String text = recognizedText.toLowerCase().trim();

        System.out.println("Processing command: " + text);

        // Check specific commands FIRST, general commands LAST

        if (text.contains("hello") || text.contains("hi")) {
            return executeHelloCommand();

        } else if (text.contains("cancel shutdown") || text.contains("stop shutdown")) {
            return executeCancelShutdown();  // ← Specific first!

        } else if (text.contains("shutdown") || text.contains("shut down")) {
            return executeShutdown();  // ← General after!

        } else if (text.contains("open notepad")) {
            return executeOpenNotepad();

        } else if (text.contains("open calculator")) {
            return executeOpenCalculator();

        } else if (text.contains("open file explorer") || text.contains("open files")) {
            return executeOpenFileExplorer();

        } else if (text.contains("create folder")) {
            return executeCreateFolder();

        } else if (text.contains("open browser") || text.contains("open chrome")) {
            return executeOpenBrowser();

        } else if (text.contains("what time") || text.contains("current time")) {
            return executeTimeCommand();

        } else if (text.contains("what date") || text.contains("today's date")) {
            return executeDateCommand();

        } else if (text.contains("close app") || text.contains("exit")) {
            return executeCloseApp();

        } else {
            return "You said: " + recognizedText;
        }
    }

    private static String executeHelloCommand(){
      return "Hello! How can i help you?";
    }

    private static String executeOpenNotepad(){
        try{
            Runtime.getRuntime().exec("notepad.exe");
            return "Opening Notepad...";
        }catch (IOException e){
            return "Failed to open NotePad";
        }
    }

    private static String executeOpenCalculator(){
        try{
            Runtime.getRuntime().exec("calc.exe");
            return "Opening Calculator...";
        }catch (IOException e){
            return "Failed to open Calculator";
        }
    }

    private static String executeOpenBrowser(){
        try{
            Runtime.getRuntime().exec("cmd /c start chrome");
            return "Opening Chrome Browser...";
        }catch (IOException e){
            return "Failed to open Browser";
        }
    }

    private static String executeTimeCommand(){
        java.time.LocalTime time = java.time.LocalTime.now();
        String timeStr = String.format("%2d:%02d", time.getHour(), time.getMinute());
        return "The current time is " + timeStr;
    }

    private static String executeDateCommand(){
        java.time.LocalDate date = java.time.LocalDate.now();
        String dateStr = date.toString();
        return "Today's date is " + dateStr;
    }

    private static String executeCloseApp(){
        System.exit(0);
        return "Closing application...";
    }

    private  static String executeShutdown(){
        try{
            Runtime.getRuntime().exec("shutdown /s /t 60"); //shutdown in 60 seconds
            return "Computer will shutdown in 60 seconds. Say 'cancel shutdown' to stop.";
        } catch (IOException e) {
            return "Failed to execute shutdown";
        }
    }

    private static String executeCancelShutdown(){
        try{
            Runtime.getRuntime().exec("shutdown /a"); // /a = ABORT SHUTDOWN
            return "Shutdown Cancelled!";
        } catch (IOException e) {
            return "Failed to cancel shutdown";
        }
    }

    private static String executeCreateFolder(){
        try{
            String userHome = System.getProperty("user.home");
            String folderPath = userHome + "\\Desktop\\NewFolder";
            Runtime.getRuntime().exec("cmd /c mkdir \"" + folderPath + "\"");
            return "Folder created on Desktop";
        }catch (IOException e){
            return "Failed to Create Folder: " + e.getMessage();
        }
    }
    
    private static String executeOpenFileExplorer(){
        try{
            Runtime.getRuntime().exec("explorer.exe");
            return "Opening file explorer";
        } catch (IOException e) {
            return "Failed to open file explorer";
        }
    }
}
