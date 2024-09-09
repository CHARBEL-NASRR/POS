package com.example.projectt;

public class PurchaseRecord {
    private String date;
    private Double totalPrice;
    private String items;
    private Integer quantity;

    public PurchaseRecord(String date, Double totalPrice, String items, Integer quantity) {
        this.date = date;
        this.totalPrice = totalPrice;
        this.items = items;
        this.quantity = quantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
