package com.example.projectt;

import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class database_admintableview {
    public Label charbelresturant;
    public Button Customers;
    public Button Menu;
    public Button Dashboard;

    public Button logout;
    @FXML
    public TextField create_email;

    @FXML
    public PasswordField create_password;

    @FXML
    private ComboBox<String> create_role;

    public TextField create_username;

    @FXML
    private TableColumn<user, Integer> user_id;

    @FXML
    private TableColumn<user, String> username;

    @FXML
    private TableColumn<user, String> email;

    @FXML
    private TableColumn<user, String> password;

    @FXML
    private TableColumn<user, String> role;

    @FXML
    private TextField search;

    public Pane pane_buttons;
    @FXML
    private TableView<user> tableview;

    private static Connection connection;
    private ObservableList<user> userList = FXCollections.observableArrayList();



    public static void connect() {
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
        Stage stage = (Stage) logout.getScene().getWindow();
        admin_tableview adminTableview = new admin_tableview();
        adminTableview.logout(stage);
    }

    public void openProductScene() throws IOException {
        Stage currentStage = (Stage) logout.getScene().getWindow();
        admin_product product = new admin_product();
        product.openProductScene(currentStage);
        handleLogout();
    }

    public void opendashboardscenne() throws IOException {
        Stage currentStage = (Stage) logout.getScene().getWindow();
        admin_dashboard admindashboard = new admin_dashboard();
        admindashboard.opendashboadscenne(currentStage);
        handleLogout();
    }


    public void openviewfeedbackscenne() throws IOException {
        Stage currentStage = (Stage)logout.getScene().getWindow();
        admin_feedback admin_feedback  = new admin_feedback();
        admin_feedback.openfeedbackscene(currentStage);
        handleLogout();
    }




    public void initialize() {
        connect();
        ObservableList<user> userList = FXCollections.observableArrayList();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("user_id");
                String name = resultSet.getString("username");
                String userEmail = resultSet.getString("emial");
                String userPassword = resultSet.getString("password");
                String userRole = resultSet.getString("role");

                userList.add(new user(id, name, userEmail, userPassword, userRole));
            }

            user_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            username.setCellValueFactory(new PropertyValueFactory<>("username"));
            email.setCellValueFactory(new PropertyValueFactory<>("email"));
            password.setCellValueFactory(new PropertyValueFactory<>("password"));
            role.setCellValueFactory(new PropertyValueFactory<>("role"));

            tableview.setItems(userList);

            search.textProperty().addListener((observable, oldValue, newValue) -> {
                ObservableList<user> filteredList = FXCollections.observableArrayList();
                for (user user : userList) {
                    if (user.getUsername().toLowerCase().contains(newValue.toLowerCase()) ||
                            user.getEmail().toLowerCase().contains(newValue.toLowerCase())) {
                        filteredList.add(user);
                    }
                }
                tableview.setItems(filteredList);
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void createaccount() {
       connect();
        try {
            String username = create_username.getText();
            String email = create_email.getText();
            String password = create_password.getText();
            String role = create_role.getValue();

            if (!isValidEmail(email)) {
                showAlert("Invalid Email", "Please enter a valid email address.");
                return;
            }

            if (!isValidPassword(password)) {
                showAlert("Invalid Password", "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");
                return;
            }

            String hashedPassword = hashPassword(password);

            String insertQuery = "INSERT INTO users (username, emial, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, hashedPassword);
            statement.setString(4, role);
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    loadDataFromDatabase();
                    exportCustomersToJson();
                    System.out.println("A new user was inserted successfully.");
                }
            } else {
                System.out.println("Failed to insert a new user.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return false;
        }

        String localPart = parts[0];
        String domainPart = parts[1];
        if (localPart.isEmpty() || domainPart.isEmpty()) {
            return false;
        }
        if (!domainPart.contains(".")) {
            return false;
        }
        if (localPart.startsWith(".") || localPart.endsWith(".") ||
                domainPart.startsWith(".") || domainPart.endsWith(".")) {
            return false;
        }
        if (domainPart.contains("..")) {
            return false;
        }
        if (domainPart.contains("@")) {
            return false;
        }
        if (domainPart.matches(".*[!#$%^&*()+=\\[\\]{};:,<>?/\"\\\\].*")) {
            return false;
        }

        return true;
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        if (password.length() < 8) {
            return false;
        }
        boolean containsUpperCase = false;
        boolean containsLowerCase = false;
        boolean containsDigit = false;
        boolean containsSpecialChar = false;
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                containsDigit = true;
            }
            else if (Character.isUpperCase(c)) {
                containsUpperCase = true;
            }
            else if (Character.isLowerCase(c)) {
                containsLowerCase = true;
            }
            else if (isSpecialChar(c)) {
                containsSpecialChar = true;
            }
            else {
                return false;
            }
        }return containsDigit && containsUpperCase && containsLowerCase && containsSpecialChar;
    }
    private boolean isSpecialChar(char c) {
        String specialChars = "@#$%^&+=!";
        return specialChars.indexOf(c) != -1;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] hashedBytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadDataFromDatabase() {
        try {
            userList.clear();
            String selectQuery = "SELECT * FROM users";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String username = resultSet.getString("username");
                String email = resultSet.getString("emial");
                String password = resultSet.getString("password");
                String role = resultSet.getString("role");
                user fetchedUser = new user(userId, username, email, password, role);
                userList.add(fetchedUser);
            }

            tableview.setItems(userList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void handleDeleteUser() {
     connect();
        user selectedUser = tableview.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete User");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete this user?");
            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                try {
                    String deleteQuery = "DELETE FROM users WHERE user_id = ?";
                    PreparedStatement statement = connection.prepareStatement(deleteQuery);
                    statement.setInt(1, selectedUser.getId());
                    int rowsDeleted = statement.executeUpdate();
                    if (rowsDeleted > 0) {

                        tableview.getItems().remove(selectedUser);
                        showAlert("Success", "User deleted successfully.");
                        loadDataFromDatabase();
                        exportCustomersToJson();
                    } else {
                        showAlert("Error", "Failed to delete user.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error", "An error occurred while deleting user.");
                }
            }
        } else {
            showAlert("Error", "Please select a user to delete.");
        }
    }


    public void exportCustomersToJson() {
        List<user> userlist = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("user_id");
                String name = resultSet.getString("username");
                String email = resultSet.getString("emial");
                String password = resultSet.getString("password");
                String role = resultSet.getString("role");
                userlist.add(new user(id, name, email, password, role));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            mapper.writeValue(new File("customers.json"), userlist);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}





