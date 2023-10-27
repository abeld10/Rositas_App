package edu.msu.diazvald.rositasapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CustomBaseAdapter extends BaseAdapter {

    private List<Item> items;
    private Context context;
    private LayoutInflater inflater;

    public CustomBaseAdapter(Context ctx, List<Item> items){
        this.context = ctx;
        this.inflater = LayoutInflater.from(ctx);
        // Check if list is null and if it is, initialize it to an empty ArrayList
        if (items != null) {
            this.items = items;
        } else {
            this.items = new ArrayList<>();
        }
    }


    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Item getItem(int position) {
        // Check if position is within the bounds of the list
        if (position >= 0 && position < items.size()) {
            return items.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate your custom layout for each item in the list
        convertView = inflater.inflate(R.layout.activity_custom_list_view, null);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IngredientFragment ingredientFragment = new IngredientFragment();
                // Passes strings in Bundle to ingredientFragment
                Bundle args = new Bundle();
                args.putString("name", items.get(position).getName());
                args.putString("price", items.get(position).getPrice());
                args.putString("description", items.get(position).getDescription());
                args.putString("imageUrl", items.get(position).getImageUrl());
                args.putInt("quantity", items.get(position).getQuantity());
                ingredientFragment.setArguments(args);

                FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.fragment_container, ingredientFragment);
                fragmentTransaction.addToBackStack(null);

                fragmentTransaction.commit();

            }
        });



        // Get references to the views in the layout
        TextView txtView = (TextView) convertView.findViewById(R.id.textName);
        ImageView itemImage = (ImageView) convertView.findViewById(R.id.imageIcon);

        // Get the current item
        Item item = getItem(position);

        // Check if the item is null
        if (item != null) {
            // If the item is not null, set the views based on the item properties
            txtView.setText(item.getName());
            // Use Glide to set images
            Glide.with(context)
                    .load(item.getImageUrl())  // getImageUrl method in Item class
                    .centerCrop()
                    .into(itemImage);
        } else {
            // If the item is null, set the views to show that there is no data
            txtView.setText("No data");
            itemImage.setImageDrawable(null); // or set to a default/placeholder image
        }

        return convertView;
    }






}
