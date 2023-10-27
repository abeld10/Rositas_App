package edu.msu.diazvald.rositasapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class BagViewModel extends ViewModel {

    public final MutableLiveData<List<Item>> cartItemsLiveData;

    public BagViewModel(){
        cartItemsLiveData = new MutableLiveData<>(new ArrayList<>());
    }
    public LiveData<List<Item>> getCartItems(){
        return cartItemsLiveData;
    }
    public void addItemToCart(Item item) {
        List<Item> currentItems = cartItemsLiveData.getValue();
        if(currentItems == null) {
            currentItems = new ArrayList<>();
        }
        currentItems.add(item);
        cartItemsLiveData.setValue(currentItems);
    }

    public void clearCart(){
        if (cartItemsLiveData.getValue() != null) {
            cartItemsLiveData.getValue().clear();
            cartItemsLiveData.setValue(cartItemsLiveData.getValue());
        }
    }


}