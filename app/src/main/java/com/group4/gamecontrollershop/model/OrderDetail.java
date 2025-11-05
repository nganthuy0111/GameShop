package com.group4.gamecontrollershop.model;

import java.io.Serializable;

public class OrderDetail implements Serializable {  // Implement Serializable
    private int id;
    private int orderId;
    private int userId;
    private int productId;
    private int quantity;
    private double price;
    private String address;
    private String phone;
    private String email;

    private String imageUrl; // Add this line

    private String productName;


    // Constructor with all fields
    public OrderDetail(int id, int orderId, int userId, int productId, int quantity, double price, String address, String phone, String email, String imageUrl,String productName) {
        this.id = id;
        this.orderId = orderId;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.imageUrl = imageUrl;
        this.productName = productName;
    }

    // Getter and Setter methods
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }


    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
