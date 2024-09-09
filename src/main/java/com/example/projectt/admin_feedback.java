package com.example.projectt;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class admin_feedback {
    private Stage primaryStage;
    private database_adminfeedback controller;



    public void openfeedbackscene(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("admin_feedback.fxml"));
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        Scene adminfeedbackscene = new Scene(root, 1000, 600);
        primaryStage.setScene(adminfeedbackscene);
        primaryStage.setTitle("Feedback");
        primaryStage.show();
    }


    public void logout(Stage primaryStage) {
        if (primaryStage != null) {
            primaryStage.close();
        }
    }

}

