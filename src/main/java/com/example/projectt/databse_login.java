package com.example.projectt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.List;
import java.util.Optional;


public class databse_login {
    private static Connection connection;
    private List<user> users;


    public databse_login(){

        users = loadUsersFromJson("customers.json");
    }



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




    public user authenticate(String email, String password) {
        for (user user : users) {
            if (user.getEmail().equals(email) && verifyPassword(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    private boolean verifyPassword(String inputPassword, String hashedPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedInputPassword = digest.digest(inputPassword.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedInputPassword) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            String hashedInputPassword64 = hexString.toString();
            if (hashedInputPassword64.length() != 64) {
                return false;
            }
            return hashedInputPassword64.equals(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<user> loadUsersFromJson(String fileName) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(new File(fileName), new TypeReference<List<user>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public user changepassword(String email, String currentPassword, String newPassword) throws SQLException {
        connect();
        user user = authenticate(email, currentPassword);
        if (user == null) {
            showAlert("Error", "Incorrect email or current password.");
            return null;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to change the password?");
        confirmationAlert.setContentText("Click OK to confirm or Cancel to abort.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            user.setPassword(newPassword);

            String sql = "UPDATE users SET password = LEFT(SHA2(?, 256), 64) WHERE emial = ? AND password = LEFT(SHA2(?, 256), 64)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, newPassword);
                statement.setString(2, email);
                statement.setString(3, currentPassword);
                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    return user;
                } else {
                    showAlert("Error", "Failed to update password.");
                    return null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while updating the password.");
                return null;
            }
        } else {
            showAlert("Info", "Password change cancelled.");
            return null;
        }
    }


    public void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}


