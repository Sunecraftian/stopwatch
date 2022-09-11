import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/* 
 * PP 23. Design and implement an application that works as a stopwatch. Include a display that shows the 
time (in seconds) as it increments. Include buttons that allow the user to start and stop the time, and 
reset the display to zero. Arrange the components to present a nice interface. Hint: use the Timer class 
to control the timing of the stopwatch.
 * 
 * 
 */

public class App extends Application implements Runnable{
    private static Scene scene;
    static String fxmlFile;
    static FXMLLoader fxmlLoader;

    public Label startButton, stopButton, lapButton, resetButton, timeDisplay;

    Thread t;

    boolean stopwatchActive = false;
    boolean stopwatchReset = true;

    long startTime, stopTime, downTime;
    long[] lapTimes = new long[100];
    int lapCount, hr, min, sec, ms;


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


    @Override
    public void run() {
        while (stopwatchActive) {
            ms = (int) (System.currentTimeMillis() - startTime);
            hr = ms / 3600000;
            min = (ms % 3600000) / 60000;
            sec = ((ms % 3600000) % 60000) / 1000;
            ms = ((ms % 3600000) % 60000) % 1000;
            Platform.runLater(() -> timeDisplay.setText(String.format("%02d:%02d:%02d:%03d", hr, min, sec, ms)));
            System.out.println(hr + ":" + min + ":" + sec + ":" + ms);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        while (!stopwatchActive) {
            downTime = System.currentTimeMillis() - stopTime;
            
        }
    }




    public void onStartTime() {
        startButton.setVisible(false);
        stopButton.setVisible(true);

        t = new Thread(App.this);
        startTime = stopwatchReset ? System.currentTimeMillis() : System.currentTimeMillis() - downTime;
        stopwatchActive = true;
        stopwatchReset = false;
        t.setDaemon(true);
        t.start();

    
        
    }

    public void onStopTime() throws InterruptedException {
        startButton.setVisible(true);
        stopButton.setVisible(false);

        stopTime = System.currentTimeMillis();
        stopwatchActive = false;
        // t.interrupt();



    }

    public void onLapTime() {

    }

    public void onResetTime() {
        stopwatchActive = false;
        stopwatchReset = true;

        timeDisplay.setText("00:00:00:000");
        

    }

    



    public static void main(String[] args) {
        launch(args);        
    }
}

