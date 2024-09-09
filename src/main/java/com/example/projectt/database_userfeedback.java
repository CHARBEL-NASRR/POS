package com.example.projectt;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class database_userfeedback {

    @FXML
    private Button Feedback;

    @FXML
    private Label Feedback_name;

    @FXML
    private Button Menu;

    @FXML
    private Label charbelrestaurant;

    @FXML
    private ComboBox<Integer> cobo_rank;

    @FXML
    private Label coment;

    @FXML
    private CheckBox dishes;

    @FXML
    private Label feedback_email;

    @FXML
    private ImageView image;

    @FXML
    private Button logout;

    @FXML
    private CheckBox nothing;

    @FXML
    private CheckBox price;

    @FXML
    private CheckBox quantity;

    @FXML
    private Label rank;

    @FXML
    private Button submit_feedback;

    @FXML
    private TextField text_comment;

    @FXML
    private TextField text_feedback_email;

    @FXML
    private TextField text_feedback_name;

    @FXML
    private Pane pane;

    user_feedback userFeedback=new user_feedback();

    private static Connection connection;


    public void connect() {
        try {
            String DB_URL = "jdbc:mysql://localhost:3306/sakila";
            String DB_USER = "root";
            String DB_PASSWORD = "1234";

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void handleLogout() {
        Stage currentStage = (Stage)pane .getScene().getWindow();
        userFeedback.logout(currentStage);
    }


    public void openmenuscenne() throws IOException {
        Stage currentStage = (Stage) pane.getScene().getWindow();
        user_menu userMenu=new user_menu();
        userMenu.openusermenuscenne(currentStage);
        handleLogout();
    }

    @FXML
    private void handleSubmitFeedback() {
        String name = text_feedback_name.getText();
        String email = text_feedback_email.getText();
        String rank = String.valueOf(cobo_rank.getValue());
        String comment = text_comment.getText();
        String selectedCheckbox = getSelectedCheckboxText();

        try {
            insertFeedback(name, email, rank, comment, selectedCheckbox);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertFeedback(String name, String email, String rank, String comment, String selectedCheckbox) throws SQLException {
        connect();
        String sql = "INSERT INTO Feedback (name, email, `rank`, comment, selected_checkbox) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, rank);
            pstmt.setString(4, comment);
            pstmt.setString(5, selectedCheckbox);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Feedback Submitted", "Feedback submitted successfully!");
            }
        }
    }




    private String getSelectedCheckboxText() {
        if (nothing.isSelected()) {
            return nothing.getText();
        } else if (price.isSelected()) {
            return price.getText();
        } else if (quantity.isSelected()) {
            return quantity.getText();
        } else if (dishes.isSelected()) {
            return dishes.getText();
        }
        return "";
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}