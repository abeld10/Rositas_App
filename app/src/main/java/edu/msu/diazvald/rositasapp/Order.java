package edu.msu.diazvald.rositasapp;

import java.util.List;

public class Order {
    private String orderId;
    private String itemName;
    private List<String> ingredients;
    private String quantity;
    private String totalPrice;
    private String personName;
    private String phoneNumber;

    private List<Item> itemsList;

    private String itemPickup;

    // Empty constructor needed for Firestore
    public Order() {}

    public Order(String orderId, String totalPrice, String personName, String phoneNumber, List<Item> itemsList, String itemPickup) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.personName = personName;
        this.phoneNumber = phoneNumber;
        this.itemsList = itemsList;
        this.itemPickup = itemPickup;
    }

    // Getters for all fields
    public List<Item> getItemsList() {
        return itemsList;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public String getItemPickup() {
        return itemPickup;
    }
    public String getOrderId() {
        return orderId;
    }
    public String getItemName() {
        return itemName;
    }

    public List<String> getIngredients() {
        return ingredients;
    }
    public String getPersonName() {
        return personName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

}
