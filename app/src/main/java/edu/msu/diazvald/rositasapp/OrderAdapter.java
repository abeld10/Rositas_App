package edu.msu.diazvald.rositasapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context context;
    private OnOrderCompleteListener onOrderCompleteListener;

    private OnOrderDeleteListener onOrderDeleteListener;



    public OrderAdapter(List<Order> orderList, OnOrderCompleteListener onOrderCompleteListener, OnOrderDeleteListener onOrderDeleteListener) {
        this.orderList = orderList;
        this.onOrderCompleteListener = onOrderCompleteListener;
        this.onOrderDeleteListener = onOrderDeleteListener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        String allItems = "";
        for (Item item : order.getItemsList()) {
            allItems += item.getQuantity() + " " + item.getName() + ": " + String.join(", ", item.getIngredients()) + "\n";
        }
        holder.itemPickUp.setText(order.getItemPickup());
        holder.itemName.setText(allItems);
        holder.totalPrice.setText( "$" + order.getTotalPrice());
        holder.personName.setText(order.getPersonName() + " - " + order.getPhoneNumber());
        holder.completeOrderButton.setOnClickListener
                (view -> {onOrderCompleteListener.onOrderComplete(order);
                holder.completeOrderButton.setVisibility(View.GONE);
                holder.orderCompleteTextView.setVisibility(View.VISIBLE);});
        holder.deleteOrderButton.setOnClickListener
                (view -> onOrderDeleteListener.onOrderDelete(order));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public interface OnOrderCompleteListener {
        void onOrderComplete(Order order);
    }

    public interface OnOrderDeleteListener {
        void onOrderDelete(Order order);
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, totalPrice, personName, itemPickUp;
        Button completeOrderButton;
        Button deleteOrderButton;
        TextView orderCompleteTextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            totalPrice = itemView.findViewById(R.id.itemPrice);
            personName = itemView.findViewById(R.id.nameNumber);
            completeOrderButton = itemView.findViewById(R.id.completeOrderButton);
            deleteOrderButton = itemView.findViewById(R.id.removeOrderButton);
            itemPickUp = itemView.findViewById(R.id.itemPickup);
            orderCompleteTextView = itemView.findViewById(R.id.orderCompleteTextView);
        }
    }
}
