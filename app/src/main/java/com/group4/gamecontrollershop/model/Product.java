package com.group4.gamecontrollershop.model;

import java.util.Date;

public class Product {
    private int id;
    private String name;
    private String description;
    private String imgUrl;
    private String detailImgUrlFirst;
    private String detailImgUrlSecond;
    private String detailImgUrlThird;
    private double oldPrice;
    private double newPrice;
    private int quantity;
    private String brand;
    private Date releaseDate;
    private String status;
    private int brandId;

    public Product() {
    }

    public Product(int id, String name, double newPrice, String imgUrl) {
        this.id = id;
        this.name = name;
        this.newPrice = newPrice;
        this.imgUrl = imgUrl;
    }

    public Product(String name, String description, String imgUrl, double oldPrice, double newPrice, int quantity, String brand, Date releaseDate, String status) {
        this.name = name;
        this.description = description;
        this.imgUrl = imgUrl;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.quantity = quantity;
        this.brand = brand;
        this.releaseDate = releaseDate;
        this.status = status;
    }

    public Product(int id, String name, String description, String imgUrl, double oldPrice, double newPrice, int quantity, String brand, Date releaseDate, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imgUrl = imgUrl;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.quantity = quantity;
        this.brand = brand;
        this.releaseDate = releaseDate;
        this.status = status;
    }

    public Product(String name, String description, String imgUrl, String detailImgUrlFirst, String detailImgUrlSecond, String detailImgUrlThird, double oldPrice, double newPrice, int quantity, String brand, Date releaseDate, String status) {
        this.name = name;
        this.description = description;
        this.imgUrl = imgUrl;
        this.detailImgUrlFirst = detailImgUrlFirst;
        this.detailImgUrlSecond = detailImgUrlSecond;
        this.detailImgUrlThird = detailImgUrlThird;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.quantity = quantity;
        this.brand = brand;
        this.releaseDate = releaseDate;
        this.status = status;
    }

    public Product(int id, String name, String description, String imgUrl, String detailImgUrlFirst, String detailImgUrlSecond, String detailImgUrlThird, double oldPrice, double newPrice, int quantity, String brand, Date releaseDate, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imgUrl = imgUrl;
        this.detailImgUrlFirst = detailImgUrlFirst;
        this.detailImgUrlSecond = detailImgUrlSecond;
        this.detailImgUrlThird = detailImgUrlThird;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.quantity = quantity;
        this.brand = brand;
        this.releaseDate = releaseDate;
        this.status = status;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public Product(int id, String name, String description, String imgUrl, String detailImgUrlFirst, String detailImgUrlSecond, String detailImgUrlThird, double oldPrice, double newPrice, int quantity, Date releaseDate, String status, int brandId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imgUrl = imgUrl;
        this.detailImgUrlFirst = detailImgUrlFirst;
        this.detailImgUrlSecond = detailImgUrlSecond;
        this.detailImgUrlThird = detailImgUrlThird;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.quantity = quantity;
        this.releaseDate = releaseDate;
        this.status = status;
        this.brandId = brandId;
    }

    public Product(String name, String description, String imgUrl, String detailImgUrlFirst, String detailImgUrlSecond, String detailImgUrlThird, double oldPrice, double newPrice, int quantity, Date releaseDate, String status, int brandId) {
        this.name = name;
        this.description = description;
        this.imgUrl = imgUrl;
        this.detailImgUrlFirst = detailImgUrlFirst;
        this.detailImgUrlSecond = detailImgUrlSecond;
        this.detailImgUrlThird = detailImgUrlThird;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.quantity = quantity;
        this.releaseDate = releaseDate;
        this.status = status;
        this.brandId = brandId;
    }

    public String getDetailImgUrlFirst() {
        return detailImgUrlFirst;
    }

    public void setDetailImgUrlFirst(String detailImgUrlFirst) {
        this.detailImgUrlFirst = detailImgUrlFirst;
    }

    public String getDetailImgUrlSecond() {
        return detailImgUrlSecond;
    }

    public void setDetailImgUrlSecond(String detailImgUrlSecond) {
        this.detailImgUrlSecond = detailImgUrlSecond;
    }

    public String getDetailImgUrlThird() {
        return detailImgUrlThird;
    }

    public void setDetailImgUrlThird(String detailImgUrlThird) {
        this.detailImgUrlThird = detailImgUrlThird;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public double getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(double newPrice) {
        this.newPrice = newPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
}
