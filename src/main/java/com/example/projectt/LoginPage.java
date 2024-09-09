package com.example.projectt;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginPage {

    private databse_login databaseLogin;
    admin_tableview tableview=new admin_tableview();
    private user_menu userMenu=new user_menu();



    public LoginPage() {
        this.databaseLogin = new databse_login();
    }

    public Scene start(Stage primaryStage) {
        StackPane root = new StackPane();
        Image backgroundImage = new Image("/1.jpg");
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.fitWidthProperty().bind(primaryStage.widthProperty());
        backgroundImageView.fitHeightProperty().bind(primaryStage.heightProperty());
        root.getChildren().add(backgroundImageView);

        VBox form = new VBox();
        form.setSpacing(10);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));

        Label titleLabel = new Label("Welcome");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setPadding(new Insets(10));
        form.getChildren().add(titleLabel);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(230);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(230);

        TextField visiblePasswordField = new TextField();
        visiblePasswordField.setEditable(false);
        visiblePasswordField.setPromptText("Password");
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setMaxWidth(230);


        HBox optionsBox = new HBox();
        optionsBox.setAlignment(Pos.CENTER);
        optionsBox.setSpacing(10);

        CheckBox showPasswordCheckbox = new CheckBox("Show Password");
        showPasswordCheckbox.setTextFill(Color.WHITE);

        Hyperlink changePasswordLink = new Hyperlink("Change Password");

        optionsBox.getChildren().addAll(showPasswordCheckbox, changePasswordLink);

        Button loginButton = new Button("Login");

        showPasswordCheckbox.setOnAction(event -> {
            if (showPasswordCheckbox.isSelected()) {
                visiblePasswordField.setText(passwordField.getText());
                passwordField.setVisible(false);
                visiblePasswordField.setVisible(true);
                visiblePasswordField.setEditable(true);

            } else {
                passwordField.setVisible(true);
                visiblePasswordField.setVisible(false);
                visiblePasswordField.setEditable(true);

            }
        });

        loginButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            handleLoginAction(email,password);
        });

        changePasswordLink.setOnAction(e -> {
            Stage changePasswordStage = new Stage();
            Scene changePasswordScene = createChangePasswordScene(changePasswordStage);
            changePasswordStage.setTitle("Change Password");
            changePasswordStage.setScene(changePasswordScene);
            changePasswordStage.show();
        });

        form.getChildren().addAll(emailField,passwordField,visiblePasswordField,optionsBox, loginButton);

        root.getChildren().add(form);

        Scene scene = new Scene(root, 600, 400);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.show();
        return scene;
    }




    public Scene createChangePasswordScene(Stage changePasswordStage) {
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setStyle("-fx-font-size: 14px; -fx-pref-height: 30px;");

        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Current Password");
        currentPasswordField.setStyle("-fx-font-size: 14px; -fx-pref-height: 30px;");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");
        newPasswordField.setStyle("-fx-font-size: 14px; -fx-pref-height: 30px;");

        Label titleLabel = new Label("Change Password");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        GridPane.setMargin(titleLabel, new Insets(20, 0, 20, 0));

        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label currentPasswordLabel = new Label("Current Password:");
        currentPasswordLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label newPasswordLabel = new Label("New Password:");
        newPasswordLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");


        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: black;");
        changePasswordButton.setOnAction(event -> {
            String email = emailField.getText();
            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();
            handleChangePasswordButtonAction(email,currentPassword,newPassword,changePasswordStage);
        });

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(titleLabel, 0, 0, 2, 1); // Span 2 columns
        GridPane.setHalignment(titleLabel, HPos.CENTER);
        gridPane.add(emailLabel, 0, 1);
        gridPane.add(emailField, 1, 1);
        gridPane.add(currentPasswordLabel, 0, 2);
        gridPane.add(currentPasswordField, 1, 2);
        gridPane.add(newPasswordLabel, 0, 3);
        gridPane.add(newPasswordField, 1, 3);
        gridPane.add(changePasswordButton, 1, 4);
        GridPane.setHalignment(changePasswordButton, HPos.CENTER); // Align button to center horizontally

        Image image = new Image("/1.jpg");
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1.0, 1.0, true, true, false, false));
        gridPane.setBackground(new Background(backgroundImage));

        Scene scene = new Scene(gridPane, 600, 400);
        changePasswordStage.setScene(scene);
        return scene;
    }



    private void handleLoginAction(String email, String password) {
        Stage primaryStage = new Stage();
        try {
            user user = databaseLogin.authenticate(email,password);
            if (user != null) {
                if ("admin".equals(user.getRole())) {
                    showAlert("Authentication Successful", "Welcome, " + user.getUsername() + "! You have successfully logged in as " + user.getRole());
                    tableview.tableview(primaryStage);
                } else if ("user".equals(user.getRole())) {
                    showAlert("Authentication Successful", "Welcome, " + user.getUsername() + "! You have successfully logged in." +user.getRole());
                    DatabaseHandler.setCurrentUser(user);
                    userMenu.openusermenuscenne(primaryStage);
                } else {
                    showAlert("Authentication Failed", "Unknown user role.");
                }
            } else {
                showAlert("Authentication Failed", "Invalid email or password.");
            }
        } catch (Exception e) {
            showAlert("Authentication Error", "An error occurred during authentication.");
            e.printStackTrace();
        }
    }






    private void handleChangePasswordButtonAction(String email, String currentPassword, String newPassword, Stage changePasswordStage) {
        try {
            user user = databaseLogin.changepassword(email, currentPassword, newPassword);
            if (user != null) {
                showAlert("New Password", "Your new password is: " + newPassword);
                MyThread backgroundThread = new MyThread(changePasswordStage);
                backgroundThread.start();
                MyThread.waitForSeconds(5);
            } else {
                showAlert("Password Change Failed", "Invalid email or current password. Please try again.");
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred while trying to change the password. Please try again later.");
            e.printStackTrace();
        }
    }





    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}





