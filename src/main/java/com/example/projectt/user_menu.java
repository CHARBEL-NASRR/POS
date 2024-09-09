package com.example.projectt;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class user_menu {

    private Stage primaryStage;

    private database_usermenu controller;

    public user currentUser;
    DatabaseHandler databaseHandler=new DatabaseHandler();


    public void openusermenuscenne(Stage primaryStage ) throws IOException {

        this.primaryStage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu.fxml"));
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.initialize();
        Scene productScene = new Scene(root, 1043, 600);
        primaryStage.setScene(productScene);
        primaryStage.setTitle("menu");
        primaryStage.show();

    }

    public void logout(Stage primaryStage) {
        if (primaryStage != null) {
            primaryStage.close();
        }
    }


}

