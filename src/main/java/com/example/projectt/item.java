package com.example.projectt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class item {


    @FXML
    private ImageView item_image;

    @FXML
    private  Label item_name;

    @FXML
    private Label item_price;

    @FXML
    private Spinner<Integer> spinner;

    @FXML
    SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100);
    private database_usermenu databaseUserMenu;

    private int id;
    private double cost;

    private int stock;

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



    public void setDatabaseUserMenu(database_usermenu databaseUserMenu) {

        this.databaseUserMenu = databaseUserMenu;
    }


    public void setProductDetails(int id, double cost, String name, double price, String imagePath,int stock) {
        this.id = id;
        this.cost = cost;
        this.stock=stock;

        item_name.setText(name);
        item_price.setText(String.format("$%.2f", price));
        spinner.setValueFactory(valueFactory);

        if (imagePath != null && !imagePath.isEmpty()) {
            Image image = new Image(imagePath);
            item_image.setImage(image);
        }
    }


    @FXML
    public void handleAddButtonAction() {
        String priceText = item_price.getText().replace("$", "");
        double price = Double.parseDouble(priceText);
        String item = item_name.getText();
        int quantity = spinner.getValue();
        double totalprice = price;

        int availableStock = getStockFromDatabase(item);

        if (availableStock >= quantity) {
            product product = new product(id, item, quantity, totalprice, cost, availableStock);
            databaseUserMenu.addProductToTableView(product);

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Stock Alert");
            alert.setHeaderText("Insufficient Stock");
            alert.setContentText("we dont have "+quantity + " " + item +" we have only " + availableStock );
            alert.showAndWait();
        }
    }

    private int getStockFromDatabase(String item) {
        connect();
        int stock = 0;
        String query = "SELECT stock FROM products WHERE product_type = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, item);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                stock = resultSet.getInt("stock");
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stock;
    }




}
