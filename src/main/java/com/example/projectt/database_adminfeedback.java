package com.example.projectt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;

public class database_adminfeedback {


    @FXML
    private TableColumn<feedback, Integer> Feedback_id;

    @FXML
    private TableColumn<feedback, Text> comment;

    @FXML
    private TableColumn<feedback, String> email;

    @FXML
    private TableColumn<feedback, String> feedback;

    @FXML
    private TableColumn<feedback,String> name;

    @FXML
    private Pane pane;
    @FXML
    private TextField search;

    @FXML
    private TableColumn<feedback, Integer> rank;


    @FXML
    private TableView<feedback> tableview;

    private static Connection connection;

    private ObservableList<feedback> feedbackList = FXCollections.observableArrayList();

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
        Stage currentStage = (Stage) pane.getScene().getWindow();
        admin_feedback admin_feedback=new admin_feedback();
        admin_feedback.logout(currentStage);
    }


    public void openproductscenne() throws IOException {
        Stage currentStage = (Stage) pane.getScene().getWindow();
        admin_product adminProduct = new admin_product();
        adminProduct.openProductScene(currentStage);
        handleLogout();
    }

    public void opentableviewscenne() {
        Stage currentStage = (Stage) pane.getScene().getWindow();
        admin_tableview adminTableview = new admin_tableview();
        adminTableview.tableview(currentStage);
        handleLogout();
    }

    public void openviewdashboardscenne() throws IOException {
        Stage currentStage = (Stage) pane.getScene().getWindow();
        admin_dashboard adminDashboard = new admin_dashboard();
        adminDashboard.opendashboadscenne(currentStage);
        handleLogout();
    }

    public void initialize() {
        Feedback_id.setCellValueFactory(new PropertyValueFactory<>("feedbackId"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        rank.setCellValueFactory(new PropertyValueFactory<>("rank"));
        comment.setCellValueFactory(new PropertyValueFactory<>("comment"));
        feedback.setCellValueFactory(new PropertyValueFactory<>("selectedCheckbox"));
        populateTableView();

        search.textProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<feedback> filteredList = FXCollections.observableArrayList();
            for (feedback feedback :feedbackList ) {
                if (feedback.getComment().toLowerCase().contains(newValue.toLowerCase()) ||
                        feedback.getEmail().toLowerCase().contains(newValue.toLowerCase())) {
                    filteredList.add(feedback);
                }
            }
            tableview.setItems(filteredList);
        });
    }


    public void populateTableView() {
        connect();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Feedback");
            while (resultSet.next()) {
                feedback feedbackItem = new feedback(
                        resultSet.getInt("feedback_id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getInt("rank"),
                        resultSet.getString("comment"),
                        resultSet.getString("selected_checkbox")
                );
                feedbackList.add(feedbackItem);
            }
            tableview.setItems(feedbackList);
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void handleDeleteFeedback() {
        connect();
        feedback selectedFeedback = tableview.getSelectionModel().getSelectedItem();
        if (selectedFeedback != null) {
                try {
                    String deleteQuery = "DELETE FROM Feedback WHERE feedback_id = ?";
                    PreparedStatement statement = connection.prepareStatement(deleteQuery);
                    statement.setInt(1, selectedFeedback.getFeedbackId());
                    int rowsDeleted = statement.executeUpdate();
                    if (rowsDeleted > 0) {
                        tableview.getItems().remove(selectedFeedback);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }



