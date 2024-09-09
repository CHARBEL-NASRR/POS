package com.example.projectt;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class user_feedback {



private Stage primaryStage;

private database_userfeedback controller;

public user currentUser;


public void openuserfeedbackscene(Stage primaryStage ) throws IOException {

    this.primaryStage = primaryStage;
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("user_feedback.fxml"));
    Parent root = fxmlLoader.load();
    controller = fxmlLoader.getController();
    Scene productScene = new Scene(root, 1043, 600);
    primaryStage.setScene(productScene);
    primaryStage.setTitle("Feedback");
    primaryStage.show();

}

public void logout(Stage primaryStage) {
    if (primaryStage != null) {
        primaryStage.close();
    }
}

}
