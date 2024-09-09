package com.example.projectt;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class admin_dashboard {

    private Stage primaryStage;

    private database_admindashboard controller;


    public void opendashboadscenne(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.updateNumberOfCustomersLabel();
        controller.calculateProfitForEach30Days();
        controller.calculateProfitLast24Hours();
        controller.calculateQuantityLast30Days();
        Scene productScene = new Scene(root, 1000, 600);
        primaryStage.setScene(productScene);
        primaryStage.setTitle("dashboard");
        primaryStage.show();
    }



    public void logout(Stage primaryStage) {
        if (primaryStage != null) {
            primaryStage.close();
        }
    }


}
