package com.example.projectt;

import javafx.application.Platform;
import javafx.stage.Stage;

public class MyThread extends Thread {

    private Stage stage;

    public MyThread(Stage stage) {
        this.stage = stage;
    }

    public void run() {
        try {
            Thread.sleep(5000);
            Platform.runLater(() -> {
                stage.close();
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void waitForSeconds(int seconds) {
        try {
            Thread.sleep( seconds* 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
