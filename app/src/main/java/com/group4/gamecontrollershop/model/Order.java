package com.group4.gamecontrollershop.model;

import java.util.Date;
import java.util.List;

public class Order {
    private int id;
    private int userId;
    private Double totalAmount;
    private Date orderDate;
    private String status;
    private List<OrderDetail> orderDetails;  // List of OrderDetails for multiple products

    // Constructor for multiple OrderDetails
    public Order(int id, int userId, Double totalAmount, Date orderDate, String status, List<OrderDetail> orderDetails) {
        this.id = id;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.status = status;
        this.orderDetails = orderDetails;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
