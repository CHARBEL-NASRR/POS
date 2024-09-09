package com.example.projectt;

public class product {
    private int productId;
    private double price;
    private double cost;
    private int stock;
    private String path;
    private String type;
    private int quantity;


    public product(int productId, double price, double cost, int stock, String path, String type) {
        this.productId = productId;
        this.price = price;
        this.cost = cost;
        this.stock = stock;
        this.path = path;
        this.type = type;
    }

    public product(int product_id ,String type,int quantity,double price,double cost,int stock){
        this.productId=product_id;
        this.type=type;
        this.quantity=quantity;
        this.price=price;
        this.cost=cost;
        this.stock=stock;
    }


    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
