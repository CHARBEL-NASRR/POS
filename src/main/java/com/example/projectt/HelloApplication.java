package com.example.projectt;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {


    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login");
        LoginPage loginPage = new LoginPage();
        Scene loginScene = loginPage.start(primaryStage);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
