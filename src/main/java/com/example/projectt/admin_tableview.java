package com.example.projectt;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class admin_tableview {
    private database_admintableview controller;
    private Stage primaryStage;



    public void tableview(Stage primaryStage) {
        try {
            this.primaryStage = primaryStage;

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("tableview.fxml"));
            Parent root = fxmlLoader.load();

            controller = fxmlLoader.getController();

            Scene tableviewScene = new Scene(root, 1000, 600);


            primaryStage.setScene(tableviewScene);
            primaryStage.setTitle("View Users");
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void logout(Stage primaryStage) {
        if (primaryStage != null) {
            primaryStage.close();
        }
    }



}
