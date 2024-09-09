package com.example.projectt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class database_adminproduct {

    @FXML
    private Pane product_pane;
    @FXML
    private TextField textprice;
    @FXML
    private TextField textstock;
    @FXML
    private TextField texttype;
    @FXML
    private TextField testcost;
    @FXML
    private Button addimage;
    @FXML
    private TextField update_stock;
    @FXML
    private TextField update_price;
    @FXML
    public TextField search;
    @FXML
    private TableColumn<product, Double> product_cost;

    @FXML
    private TableColumn<product, String> product_path;

    @FXML
    private TableColumn<product, Double> product_price;

    @FXML
    private TableColumn<product, Integer> product_stock;

    @FXML
    private TableColumn<product, Integer> product_id;
    public TableColumn<product, String> product_type;

    @FXML
    private TableView<product> pruduct_view;


    private ImageView imageView = new ImageView();

    private String imagePath;
    private static Connection connection;

    admin_product adminProduct = new admin_product();

    ObservableList<product> productlist = FXCollections.observableArrayList();


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



    public void initialize() {
        pruduct_view.setRowFactory(tv -> {
            TableRow<product> row = new TableRow<product>() {
                protected void updateItem(product item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty && item != null) {
                        int stock = item.getStock();

                        if (stock <= 5 && stock > 0) {
                            setStyle("-fx-background-color: orange;");
                        } else if (stock == 0) {
                            setStyle("-fx-background-color: red;");
                        } else {
                            setStyle("");
                        }
                    } else {
                        setStyle("");
                    }
                }
            };

            search.textProperty().addListener((observable, oldValue, newValue) -> {
                ObservableList<product> filteredList = FXCollections.observableArrayList();
                for (product product : productlist) {
                    if (product.getType().toLowerCase().contains(newValue.toLowerCase())) {
                        filteredList.add(product);
                    }
                }
                pruduct_view.setItems(filteredList);
            });

            return row;
        });
    }





    public void handleLogout() {
       Stage currentStage = (Stage) product_pane.getScene().getWindow();
        adminProduct.logout(currentStage);
    }




    public void opentableviewscenne() {
        Stage currentStage = (Stage) product_pane.getScene().getWindow();
        admin_tableview adminTableview=new admin_tableview();
        adminTableview.tableview(currentStage);
        handleLogout();
    }


    public void openviewdashboardscenne() throws IOException {
        Stage currentStage = (Stage) product_pane.getScene().getWindow();
        admin_dashboard adminDashboard = new admin_dashboard();
        adminDashboard.opendashboadscenne(currentStage);
        handleLogout();
    }

    public void openfeedbackscene() throws IOException {
        Stage currentStage = (Stage) product_pane.getScene().getWindow();
        admin_feedback Adminfeedback=new admin_feedback();
        Adminfeedback.openfeedbackscene(currentStage);
        handleLogout();
    }




    @FXML
    private void handleAddImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        File selectedFile = fileChooser.showOpenDialog(addimage.getScene().getWindow());
        if (selectedFile != null) {
            imagePath = selectedFile.toURI().toString();
            imageView.setImage(new Image(imagePath));
        }
    }

    public void createProduct() {
        connect();
        try {
            double price = Double.parseDouble(textprice.getText());
            double cost = Double.parseDouble(testcost.getText());
            int stock = Integer.parseInt(textstock.getText());
            String path = imagePath;
            String type = texttype.getText();

            String insertQuery = "INSERT INTO products (price, cost, stock, path, product_type) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            statement.setDouble(1, price);
            statement.setDouble(2, cost);
            statement.setInt(3, stock);
            statement.setString(4, path);
            statement.setString(5, type);

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    int productId = rs.getInt(1);
                    product newProduct = new product(productId, price, cost, stock, path, type);
                    pruduct_view.getItems().add(newProduct);
                    load();
                    System.out.println("A new product was inserted successfully.");
                }
            } else {
                System.out.println("Failed to insert a new product.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void load() {
        connect();
        productlist.clear();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM products");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("product_id");
                double price = resultSet.getDouble("price");
                double cost = resultSet.getDouble("cost");
                int stock = resultSet.getInt("stock");
                String path = resultSet.getString("path");
                String type = resultSet.getString("product_type");

                productlist.add(new product(id, price ,cost, stock, path, type));
            }

            product_id.setCellValueFactory(new PropertyValueFactory<>("productId"));
            product_price.setCellValueFactory(new PropertyValueFactory<>("price"));
            product_cost.setCellValueFactory(new PropertyValueFactory<>("cost"));
            product_stock.setCellValueFactory(new PropertyValueFactory<>("stock"));
            product_path.setCellValueFactory(new PropertyValueFactory<>("path"));
            product_type.setCellValueFactory(new PropertyValueFactory<>("type"));


            pruduct_view.setItems(productlist);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void handleDeleteProduct() {
        connect();
        product selectedProduct = pruduct_view.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Product");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete this product?");
            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                try {
                    String deleteQuery = "DELETE FROM products WHERE product_id = ?";
                    PreparedStatement statement = connection.prepareStatement(deleteQuery);
                    statement.setInt(1, selectedProduct.getProductId());
                    int rowsDeleted = statement.executeUpdate();
                    if (rowsDeleted > 0) {
                        pruduct_view.getItems().remove(selectedProduct);
                        showAlert("Success", "Product deleted successfully.");
                        load();
                    } else {
                        showAlert("Error", "Failed to delete product.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error", "An error occurred while deleting product.");
                }
            }
        } else {
            showAlert("Error", "Please select a product to delete.");
        }
    }



    @FXML
    private void updateProduct() {
        product selectedProduct = pruduct_view.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            try {
                double updatedPrice = Double.parseDouble(update_price.getText());
                int updatedStock = Integer.parseInt(update_stock.getText());
                connect();
                String updateQuery = "UPDATE products SET price = ?, stock = ? WHERE product_id = ?";
                PreparedStatement statement = connection.prepareStatement(updateQuery);
                statement.setDouble(1, updatedPrice);
                statement.setInt(2, updatedStock);
                statement.setInt(3, selectedProduct.getProductId());

                int rowsUpdated = statement.executeUpdate();

                if (rowsUpdated > 0) {
                    selectedProduct.setPrice(updatedPrice);
                    selectedProduct.setStock(updatedStock);
                    load();
                    showAlert("Success", "Product updated successfully.");
                } else {
                    showAlert("Error", "Failed to update product.");
                }
            } catch (SQLException | NumberFormatException e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while updating product.");
            }
        } else {
            showAlert("Error", "Please select a product to update.");
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




