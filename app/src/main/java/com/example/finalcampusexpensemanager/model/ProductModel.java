package com.example.finalcampusexpensemanager.model;

public class ProductModel {
    private int id;
    private String name;
    private String image;
    private int price;

    public ProductModel(int idPd, String namePd, int pricePd, String imgPd) {
        this.id = idPd;
        this.name = namePd;
        this.price = pricePd;
        this.image = imgPd;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
