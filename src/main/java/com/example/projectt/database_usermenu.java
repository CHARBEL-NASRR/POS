package com.example.projectt;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;

public class database_usermenu {

    @FXML
    private AnchorPane anchor_isertmenu;

    @FXML
    private AnchorPane anchor_menu;

    @FXML
    private AnchorPane anchor_table;

    @FXML
    private Button delete;

    @FXML

    private ImageView image;

    @FXML
    private Label mony;

    @FXML
    public Button pay;

    @FXML
    private TableColumn<product, Double> price;

    @FXML
    private TableColumn<product, String> product_name;

    @FXML
    private TableColumn<product,Integer> quantity;

    @FXML
    private ScrollPane scrolepane;

    @FXML
    private TableView<product> table_view;

    @FXML
    private Label total;
    @FXML
    private Button clear;
    private static Connection connection;
    public GridPane gridpane;
    ObservableList<product> products = FXCollections.observableArrayList();

   user currentUser;



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

    @FXML
    public void initialize() {
        connect();
        loadItems();
    }

    private void loadItems() {
        try {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT product_id ,price ,cost,stock, path,product_type  FROM products");

            int row = 0;
            int column = 0;
            while (resultSet.next()) {
                int id = resultSet.getInt("product_id");
                double price = resultSet.getDouble("price");
                double cost = resultSet.getDouble("cost");
                int stock=resultSet.getInt("stock");
                String imagePath = resultSet.getString("Path");
                String name = resultSet.getString("product_type");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("item.fxml"));
                Parent itemNode = loader.load();

                item itemController = loader.getController();
                itemController.setProductDetails(id,cost,name, price, imagePath,stock);
                itemController.setDatabaseUserMenu(this);

                gridpane.add(itemNode, column, row);

                column++;
                if (column >= 3) {
                    column = 0;
                    row++;
                }
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleLogout() {
        Stage currentStage = (Stage)anchor_isertmenu.getScene().getWindow();
        user_menu userMenu= new user_menu();
        userMenu.logout(currentStage);
    }
    public void openfeedbackscene() throws IOException {
        Stage currentStage = (Stage) anchor_isertmenu.getScene().getWindow();
        user_feedback userFeedback=new user_feedback();
        userFeedback.openuserfeedbackscene(currentStage);
        handleLogout();
    }




    public void addProductToTableView(product product ) {
        double totalPrice = product.getPrice() * product.getQuantity();
        product_name.setCellValueFactory(new PropertyValueFactory<>("type"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        price.setCellValueFactory(cellData -> new SimpleDoubleProperty(totalPrice).asObject());
        products.add(product);
        table_view.setItems(products);
        calculateTotalPrice();
    }


    public double  calculateTotalPrice() {
        double totalPrice = 0;
        for (product prod : products) {
            totalPrice += prod.getPrice()* prod.getQuantity();
        }
        mony.setText(String.format("$%.2f", totalPrice));

        return  totalPrice;
    }

    public void handleDeleteButtonAction() {
        product selectedProduct = table_view.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            products.remove(selectedProduct);
            calculateTotalPrice();
        }
    }

    public void handleClearButtonAction() {
        products.clear();
        calculateTotalPrice();
    }


    public void handlePayButtonAction() {
        this.currentUser = DatabaseHandler.getCurrentUser();
        try {
            connect();
            double totalPrice = 0;
            Stage detailsStage = new Stage();
            detailsStage.initModality(Modality.APPLICATION_MODAL);
            detailsStage.setTitle("Purchase Details");

            ImageView imageView = new ImageView(new Image("file:src/main/resources/do.jpg"));
            imageView.fitWidthProperty().bind(detailsStage.widthProperty());
            imageView.fitHeightProperty().bind(detailsStage.heightProperty());

            VBox detailsLayout = new VBox(10);
            detailsLayout.setAlignment(Pos.CENTER);
            detailsLayout.setPadding(new Insets(20));

            Label thankYouLabel = new Label("Thank you for joining our restaurant!");
            thankYouLabel.setTextFill(Color.WHITE);
            detailsLayout.getChildren().add(thankYouLabel);

            for (product prod : products) {
                int quantity = prod.getQuantity();
                double unitPrice = prod.getPrice();
                double itemTotal = quantity * unitPrice;
                totalPrice += itemTotal;

                Label itemLabel = new Label(prod.getType() + " - Quantity: " + quantity + " - Total Price: $" + itemTotal);
                itemLabel.setTextFill(Color.WHITE);
                detailsLayout.getChildren().add(itemLabel);
            }

            Label totalPriceLabel = new Label("Total Price: $" + totalPrice);
            totalPriceLabel.setTextFill(Color.WHITE);
            detailsLayout.getChildren().add(totalPriceLabel);

            Button saveButton = new Button("Save");
            saveButton.setOnAction(event -> savePurchaseDetailsToFile(detailsLayout));
            detailsLayout.getChildren().add(saveButton);

            StackPane root = new StackPane();
            root.getChildren().addAll(imageView, detailsLayout);

            Scene detailsScene = new Scene(root, 400, 400);
            detailsStage.setScene(detailsScene);
            detailsStage.show();
            MyThread thread=new MyThread(detailsStage);
            thread.start();
            insertOrderIntoDatabase(totalPrice);
            updateStockInDatabase();
            products.clear();

            System.out.println("Purchase successful!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Purchase failed!");
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }



    private void insertOrderIntoDatabase(double totalPrice) throws SQLException {
        for (product prod : products) {
            int quantity = prod.getQuantity();
            double unitPrice = prod.getPrice();
            double cost = prod.getCost();
            int productId = prod.getProductId();
            double totalItemPrice = quantity * unitPrice;
            double profit = totalItemPrice - (quantity * cost);

            String insertOrderQuery = "INSERT INTO ordersss (product_id, customer_id, quantity, total_price, profit, date_of_purchase) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(insertOrderQuery);
            pstmt.setInt(1, productId);
            pstmt.setInt(2, currentUser.getId());
            pstmt.setInt(3, quantity);
            pstmt.setDouble(4, totalItemPrice);
            pstmt.setDouble(5, profit);
            pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.executeUpdate();
            pstmt.close();
        }
    }

    private void updateStockInDatabase() throws SQLException {
        for (product prod : products) {
            int quantity = prod.getQuantity();
            int productId = prod.getProductId();

            String updateStockQuery = "UPDATE products SET stock = stock - ? WHERE product_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(updateStockQuery);
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, productId);
            pstmt.executeUpdate();
            pstmt.close();
        }
    }

    private void savePurchaseDetailsToFile(VBox detailsLayout) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Purchase Details");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                for (javafx.scene.Node node : detailsLayout.getChildren()) {
                    if (node instanceof Label) {
                        writer.write(((Label) node).getText() + "\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}