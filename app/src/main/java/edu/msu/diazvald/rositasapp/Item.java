package edu.msu.diazvald.rositasapp;

import static java.lang.Float.parseFloat;

import java.util.ArrayList;
import java.util.List;

public class Item {

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    private String description;

    public void setDescription(String description) {
        this.description = description;
    }

    private String price;

    public float getPriceFloat() {
        return priceFloat;
    }

    public void setPriceFloat(float priceFloat) {
        this.priceFloat = priceFloat;
    }

    private float priceFloat;


    public float getTotalPrice() {
        return priceFloat * quantity;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    private float totalPrice;



    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    private int quantity = 1;

    private List<String> ingredients = new ArrayList<>();

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }


    public void setPrice(String price) {
        this.price = price;
        priceFloat = parseFloat(price);
        totalPrice = priceFloat * quantity;
    }

    private String id;

    public Item() {

    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }


    public String getId() {
        return id;
    }

    private String imageUrl;

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
