package com.example.projectt;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class admin_product {

    private Stage primaryStage;
    private database_adminproduct controller;




    public void openProductScene(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("product.fxml"));
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.load();
        Scene productScene = new Scene(root, 1000, 600);
        primaryStage.setScene(productScene);
        primaryStage.setTitle("Product");
        primaryStage.show();
    }


    public void logout(Stage primaryStage) {
        if (primaryStage != null) {
            primaryStage.close();
        }
    }

}
