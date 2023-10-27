package edu.msu.diazvald.rositasapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView orderRecyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        orderRecyclerView = findViewById(R.id.ordersRecyclerView);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(orderList, this::completeOrder, this::deleteOrder);
        orderRecyclerView.setAdapter(orderAdapter);


        fetchOrders();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void fetchOrders() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Handle the error
                        return;
                    }

                    orderList.clear();
                    // loop through firebase document
                    for (QueryDocumentSnapshot doc : value) {
                        String orderId = doc.getId();
                        String personName = doc.getString("Name");
                        String phoneNumber = doc.getString("Number");
                        String totalPrice = doc.getString("Price");
                        String itemPickUp = doc.getString("Option");
                        String itemName = "";
                        long quantity;
                        List<String> ingredients = new ArrayList<>();

                        List<Map<String, Object>> itemsListMap = (List<Map<String, Object>>) doc.get("Items");
                        List<Item> itemsList = new ArrayList<>();
                        for (Map<String, Object> itemMap : itemsListMap) {
                            itemName = (String) itemMap.get("name");
                            ingredients = (List<String>) itemMap.get("ingredients");
                            quantity = (long) itemMap.get("quantity");
                            Item item = new Item();
                            item.setName(itemName);
                            item.setIngredients(ingredients);
                            item.setQuantity((int) quantity);
                            itemsList.add(item);
                        }

                        // Now, you can create an Order object using the above data.
                        // Note: You might need to modify the Order class or its constructor to accommodate this data structure.
                        Order order = new Order(orderId, totalPrice, personName, phoneNumber, itemsList, itemPickUp);
                        orderList.add(order);
                    }
                    orderAdapter.notifyDataSetChanged();
                });
    }


    private void deleteOrder(Order order) {
        // Remove order from Firestore and notify the user
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders").document(order.getOrderId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Notify the user. This is a placeholder. Implement your notification logic.
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });
    }

    private void completeOrder(Order order) {
        // Update the order's status to "complete" in Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders").document(order.getOrderId())
                .update("status", "complete")
                .addOnSuccessListener(aVoid -> {
                    // Notify the user or perform other actions if needed
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });
    }



}
