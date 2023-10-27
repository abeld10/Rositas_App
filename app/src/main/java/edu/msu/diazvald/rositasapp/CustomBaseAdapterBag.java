package edu.msu.diazvald.rositasapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CustomBaseAdapterBag extends RecyclerView.Adapter<CustomBaseAdapterBag.CustomViewHolder> {


    // List of your items
    private List<Item> cartItems;
    private Context context;

    private OnItemQuantityChangedListener listener;


    // interface
    public interface OnItemQuantityChangedListener {
        void onItemQuantityChanged();
    }


    // Constructor for your adapter
    public CustomBaseAdapterBag(Context context, List<Item> cartitems, OnItemQuantityChangedListener listener) {
        // Assign your items list to the adapter's one
        this.context = context;
        this.listener = listener;

        if (cartitems != null) {
            this.cartItems = cartitems;
        } else {
            this.cartItems = new ArrayList<>();
        }
    }

    public void updateData(List<Item> items) {
        this.cartItems.clear();
        this.cartItems.addAll(items);
        notifyDataSetChanged();
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {
        // Here you define your view items present in your layout
        TextView name, price, quantity, ingredients;
        ImageView image;

        Button addButton, removeButton;


        CustomViewHolder(View itemView) {
            super(itemView);
            // Link the objects to the views in your layout
            name = itemView.findViewById(R.id.cart_item_name);
            price = itemView.findViewById(R.id.cart_item_price);
            quantity = itemView.findViewById(R.id.cart_item_quantity);
            image = itemView.findViewById(R.id.cart_item_image);
            addButton = itemView.findViewById(R.id.add_button);
            removeButton = itemView.findViewById(R.id.remove_button);
            ingredients = itemView.findViewById(R.id.ingredients);
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate your item layout and return the view holder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        // Bind data to item at given position
        Item cartItem = cartItems.get(position);
        holder.name.setText(cartItem.getName());
        holder.quantity.setText(String.valueOf(cartItem.getQuantity()));
        Glide.with(context).load(cartItem.getImageUrl()).centerCrop().into(holder.image);
        holder.price.setText("$" + String.format("%.2f", cartItem.getTotalPrice()));
        String allIngredients = "";
        if (cartItem.getIngredients() != null) {
            for (String ingredient : cartItem.getIngredients()) {
                allIngredients += ingredient + ", ";

            }
        }
        holder.ingredients.setText(allIngredients);



        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = cartItem.getQuantity();
                cartItem.setQuantity(currentQuantity + 1);
                holder.quantity.setText(String.valueOf(cartItem.getQuantity()));
                // update price accordingly
                holder.price.setText("$" + String.format("%.2f", cartItem.getTotalPrice()));

                if(listener != null){
                    listener.onItemQuantityChanged();
                }


            }
        });

        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = cartItem.getQuantity();
                if (currentQuantity > 1) {
                    cartItem.setQuantity(currentQuantity - 1);
                    holder.quantity.setText(String.valueOf(cartItem.getQuantity()));
                    // update price accordingly
                    holder.price.setText("$" + String.format("%.2f", cartItem.getTotalPrice()));
                    if(listener != null){
                        listener.onItemQuantityChanged();

                    }
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        // Return the total number of items
        return cartItems.size();
    }
}
