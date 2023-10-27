package edu.msu.diazvald.rositasapp;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CheckoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckoutFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button confirmButton;

    private Button cancelButton;

    private TextView subtotalText;

    private TextView taxText;

    private TextView totalText;
    private TextView pickupText;


    private BagViewModel bagViewModel;

    private EditText orderNameView;
    private String orderName;
    private EditText orderNumberView;

    private String orderNumber;

    private double subtotal;
    private double tax;
    private double total;
    private boolean isPickup;

    private String summary = "";
    private TextView summaryText;

    private String userDocumentID = "";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public CheckoutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CheckoutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CheckoutFragment newInstance(String param1, String param2) {
        CheckoutFragment fragment = new CheckoutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            subtotal = Double.parseDouble(getArguments().getString("subTotal"));
            isPickup = getArguments().getBoolean("isPickup");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);
        // Inflate the layout for this fragment
        confirmButton = (Button) view.findViewById(R.id.confirm_button);
        cancelButton = (Button) view.findViewById(R.id.cancel_button);
        orderNameView = (EditText) view.findViewById(R.id.orderName);
        orderNumberView = (EditText) view.findViewById(R.id.orderNumber);

        tax = subtotal * 0.06;
        total = subtotal + tax;
        subtotalText = view.findViewById(R.id.subtotal);
        taxText = view.findViewById(R.id.taxes);
        totalText = view.findViewById(R.id.total);
        subtotalText.setText("Subtotal: $" + String.format("%.2f", subtotal));
        taxText.setText("Tax: $" + String.format("%.2f", tax));
        totalText.setText("Total: $" +  String.format("%.2f", total));
        pickupText = view.findViewById(R.id.pickup_or_dine_in);
        if (isPickup) {
            pickupText.setText("Pickup");
        }
        else {
            pickupText.setText("Dine-In");
        }
        summaryText = view.findViewById(R.id.order_summary);






        bagViewModel = new ViewModelProvider(requireActivity()).get(BagViewModel.class);
        for (Item item : bagViewModel.getCartItems().getValue()) {
            summary += item.getName() + " x" + item.getQuantity() + " " + item.getIngredients() + "\n";
        }
        summaryText.setText("Order Summary: " + summary);


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Item> items = bagViewModel.getCartItems().getValue(); // Assuming getCartItems() returns LiveData<List<Item>>
                orderName = String.valueOf(orderNameView.getText());
                orderNumber = String.valueOf(orderNumberView.getText());

                // function for adding items to the database
                uploadOrderToFirestore(items , orderName, orderNumber, total);

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();


                BagFragment bagFragment = new BagFragment();
                bagFragment.setArguments(args);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.fragment_container, bagFragment);
                fragmentTransaction.addToBackStack(null);
                BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
                bottomNavigationView.setSelectedItemId(R.id.action_bag);


                fragmentTransaction.commit();
            }
        });


        return view;
    }


    private void uploadOrderToFirestore(List<Item> items, String userName, String userNumber, double totalPrice) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new order object
        Map<String, Object> order = new HashMap<>();
        order.put("Name", userName);
        order.put("Number", userNumber);
        order.put("Price", String.format("%.2f", totalPrice));

        if (isPickup) {
            order.put("Option", "Pickup");
        }
        else {
            order.put("Option", "Dine-In");
        }

        // Convert the list of items into a list of maps
        List<Map<String, Object>> itemsList = new ArrayList<>();
        for (Item item : items) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("name", item.getName());
            itemMap.put("ingredients", item.getIngredients());
            itemMap.put("quantity", item.getQuantity());
            itemsList.add(itemMap);
        }
        order.put("Items", itemsList);

        // Add the order to Firestore
        db.collection("orders").add(order)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(getContext(), "Order placed successfully", Toast.LENGTH_SHORT).show();
                        userDocumentID = documentReference.getId();

                        Bundle args = new Bundle();
                        args.putString("userDocumentId", userDocumentID);
                        args.putString("summary", summary);
                        args.putString("total", String.format("%.2f", total));

                        ConfirmationFragment confirmationFragment = new ConfirmationFragment();
                        confirmationFragment.setArguments(args);

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        fragmentTransaction.replace(R.id.fragment_container, confirmationFragment);
                        fragmentTransaction.addToBackStack(null);
                        BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
                        bottomNavigationView.setSelectedItemId(R.id.action_bag);

                        fragmentTransaction.commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(getContext(), "Error placing order", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
