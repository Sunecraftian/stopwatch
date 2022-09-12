import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class App extends Application implements Runnable{
    // JavaFX components
    private static Scene scene;
    static String fxmlFile;
    static FXMLLoader fxmlLoader;

    public Label startButton, stopButton, lapButton, resetButton, timeDisplay, defaultTimeDisplay;
    public ListView<String> lapList;

    // Thread
    Thread t;

    // Variables
    boolean stopwatchReset = true;
    String[] lapTimes = new String[1000];
    int lapCount = 1, 
        ms = 0,
        sec = 0, 
        min = 0, 
        hr = 0; 

    
    // JavaFX start method
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Stopwatch");
        scene = new Scene(loadFXML("Stopwatch"), 600, 400);

        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }


    // Thread run method
    @Override
    public void run() {
        while (!stopwatchReset) {
            try {
                Thread.sleep(1);
                if (ms == 1000) {
                    ms = 0;
                    sec++;
                }
                if (sec == 60) {
                    sec = 0;
                    min++;
                }
                if (min == 60) {
                    min = 0;
                    hr++;
                }
                Platform.runLater(() -> timeDisplay.setText(String.format("%02d:%02d:%02d:%03d", hr, min, sec, ms)));
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error in thread");
            }
            ms++;
        }
    }

    // Start button
    public void onStartTime() {
        stopwatchReset = false;
        startButton.setVisible(false);
        stopButton.setVisible(true);
        defaultTimeDisplay.setVisible(false);

        t = new Thread(App.this);
        t.setDaemon(true);
        t.start();
    
        
    }

    // Stop button
    public void onStopTime() {
        stopwatchReset = true;
        startButton.setVisible(true);
        stopButton.setVisible(false);

    }

    // Lap button
    public void onLapTime() {
        lapTimes[lapCount-1] = timeDisplay.getText();
        if (lapCount == 1) {
            lapList.getItems().add("Lap " + lapCount + ": " + lapTimes[lapCount-1] + " - " + timeDisplay.getText());
        } else {
            lapList.getItems().add("Lap " + lapCount + ": " + (lapDifference(timeDisplay.getText(), lapTimes[lapCount-2])) + " - " + timeDisplay.getText());
        }

        lapList.scrollTo(lapCount-1);
        
        lapCount++;
    }

    // Calculate lap difference
    String lapDifference(String currTime, String prevLap) {
        int[] currWatTime = new int[4]; // Current watch time
        int[] prevLapTime = new int[4]; // Previous lap time

        long currTimeMs = 0, prevLapMs = 0;
        long difference = 0;

        String[] currWatTimeSplit = currTime.split(":"); 
        String[] prevLapTimeSplit = prevLap.split(":");

        for (int i = 0; i < 4; i++) {
            currWatTime[i] = Integer.parseInt(currWatTimeSplit[i]);
            prevLapTime[i] = Integer.parseInt(prevLapTimeSplit[i]);
        }

        currTimeMs = currWatTime[0] * 3600000 + currWatTime[1] * 60000 + currWatTime[2] * 1000 + currWatTime[3]; // Convert Current Time to Milliseconds
        prevLapMs = prevLapTime[0] * 3600000 + prevLapTime[1] * 60000 + prevLapTime[2] * 1000 + prevLapTime[3]; // Convert Previous Lap Time to Milliseconds

        difference = currTimeMs - prevLapMs; // Difference in milliseconds


        return String.format("%02d:%02d:%02d:%03d", difference / 3600000, (difference % 3600000) / 60000, (difference % 60000) / 1000, difference % 1000); // Convert Difference to String
        
    }

    // Reset button
    public void onResetTime() {
        onStopTime(); // Stop the stopwatch and reset the buttons
        defaultTimeDisplay.setVisible(true);

        lapList.getItems().clear(); // Clear the lap list
        // Reset the variables
        lapCount = 1; 
        ms = 0;
        sec = 0;
        min = 0;
        hr = 0;
        
        timeDisplay.setText(String.format("%02d:%02d:%02d:%03d", hr, min, sec, ms)); // Reset the time display

    }

    public static void main(String[] args) {
        launch(args);        
    }
}

