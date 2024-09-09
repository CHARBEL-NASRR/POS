package com.example.projectt;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.io.IOException;
import java.sql.*;

public class database_admindashboard {

    @FXML
    private Label change_numberofcust;

    @FXML
    private Label change_numberofsoldproduct;

    @FXML
    private Label change_todayincome;

    @FXML
    private Label change_totalincome;

    @FXML
    private Pane pane;

    public TextField idfeild;


    public TableView<PurchaseRecord> purchase_per_month;

    @FXML
    private TableColumn<PurchaseRecord, String> dateColumn;

    @FXML
    private TableColumn<PurchaseRecord, Double> totalPriceColumn;

    @FXML
    private TableColumn<PurchaseRecord, String> itemsColumn;

    @FXML
    private TableColumn<PurchaseRecord, Integer> quantityColumn;


    public TableView<PurchaseRecord>customer_purchase;
    @FXML
    private TableColumn<PurchaseRecord, Double> customer_date;

    @FXML
    private TableColumn<PurchaseRecord, Double> customer_items;

    @FXML
    private TableColumn<PurchaseRecord, Double> customer_quantity;

    private static Connection connection;

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
        Stage currentStage = (Stage) pane.getScene().getWindow();
        admin_dashboard adminDashboard = new admin_dashboard();
        adminDashboard.logout(currentStage);
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


    public void openviewfeedbackscenne() throws IOException {
        Stage currentStage = (Stage) pane.getScene().getWindow();
        admin_feedback admin_feedback=new admin_feedback();
        admin_feedback.openfeedbackscene(currentStage);
        handleLogout();
    }





    public void updateNumberOfCustomersLabel() {
        String query = "SELECT COUNT(*) FROM users";
        int numberOfCustomers = 0;
        connect();
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                numberOfCustomers = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        change_numberofcust.setText(String.valueOf(numberOfCustomers));
    }


    public void calculateProfitForEach30Days() {
        connect();
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = LocalDate.of(2024, 5, 1);

        double totalProfit = 0;

        try {
            String query = "SELECT SUM(profit) FROM ordersss WHERE date_of_purchase BETWEEN ? AND ?";
            PreparedStatement pstmt = connection.prepareStatement(query);

            while (startDate.isBefore(currentDate)) {
                LocalDate periodEndDate = startDate.plusDays(29);
                pstmt.setDate(1, Date.valueOf(startDate));
                pstmt.setDate(2, Date.valueOf(periodEndDate));
                ResultSet resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    totalProfit += resultSet.getDouble(1);
                }
                resultSet.close();
                startDate = periodEndDate.plusDays(1);
            }

            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        change_totalincome.setText(String.valueOf(totalProfit));
    }


    public void calculateProfitLast24Hours() {
        connect();
        double totalProfit = 0;

        try {
            String query = "SELECT SUM(profit) FROM ordersss WHERE date_of_purchase >= NOW() - INTERVAL 1 DAY";
            PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                totalProfit = resultSet.getDouble(1);
            }
            resultSet.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        change_todayincome.setText(String.valueOf(totalProfit));
    }


    public void calculateQuantityLast30Days() {
        connect();
        int totalQuantity = 0;

        try {
            String query = "SELECT SUM(quantity)  FROM ordersss WHERE date_of_purchase >= NOW() - INTERVAL 30 DAY";
            PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                totalQuantity = resultSet.getInt(1);
            }
            resultSet.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        change_numberofsoldproduct.setText(String.valueOf(totalQuantity));
    }


    public void initialize() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        itemsColumn.setCellValueFactory(new PropertyValueFactory<>("items"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        populateTableView();
    }

    private void populateTableView() {
        connect();
        try {
            String query = "SELECT DATE(ordersss.date_of_purchase), " +
                    "products.product_type, " +
                    "SUM(ordersss.total_price), " +
                    "SUM(ordersss.quantity) " +
                    "FROM ordersss " +
                    "INNER JOIN products ON ordersss.product_id = products.product_id " +
                    "GROUP BY DATE(ordersss.date_of_purchase), products.product_type";

            try (PreparedStatement pstmt = connection.prepareStatement(query);
                 ResultSet resultSet = pstmt.executeQuery()) {

                while (resultSet.next()) {
                    String date = resultSet.getString(1);
                    String item = resultSet.getString(2);
                    double totalPrice = resultSet.getDouble(3);
                    int quantity = resultSet.getInt(4);

                    boolean existingRecordFound = false;
                    for (PurchaseRecord record : purchase_per_month.getItems()) {
                        if (record.getDate().equals(date) && record.getItems().equals(item)) {
                            record.setQuantity(record.getQuantity() + quantity);
                            record.setTotalPrice(record.getTotalPrice() + totalPrice);
                            existingRecordFound = true;
                            break;
                        }
                    }

                    if (!existingRecordFound) {
                        purchase_per_month.getItems().add(new PurchaseRecord(date, totalPrice, item, quantity));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void showUserPurchase() {
        String userId = idfeild.getText();

        if (userId.isEmpty()) {
            return;
        }

        connect();
        try {
            String query = "SELECT DATE(ordersss.date_of_purchase), " +
                    "products.product_type, " +
                    "SUM(ordersss.total_price), " +
                    "SUM(ordersss.quantity) " +
                    "FROM ordersss  " +
                    "INNER JOIN products  ON ordersss.product_id = products.product_id " +
                    "WHERE ordersss.customer_id = ? " +
                    "GROUP BY DATE(ordersss.date_of_purchase), products.product_type";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, userId);
                ResultSet resultSet = pstmt.executeQuery();
                customer_purchase.getItems().clear();

                while (resultSet.next()) {
                    String date = resultSet.getString(1);
                    String item = resultSet.getString(2);
                    double totalPrice = resultSet.getDouble(3);
                    int quantity = resultSet.getInt(4);

                    customer_purchase.getItems().add(new PurchaseRecord(date, totalPrice, item, quantity));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void customerinitialize() {
        customer_date.setCellValueFactory(new PropertyValueFactory<>("date"));
        customer_items.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        customer_items.setCellValueFactory(new PropertyValueFactory<>("items"));
        customer_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        showUserPurchase();
    }



}

































































