package com.group4.gamecontrollershop.model;

public class Favorite {
    private int id;

    private int userId;
    private User user;

    private int productId;
    private Product product;

    public Favorite() {
    }

    public Favorite(int id, int userId, User user, int productId, Product product) {
        this.id = id;
        this.userId = userId;
        this.user = user;
        this.productId = productId;
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

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

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
