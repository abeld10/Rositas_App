package edu.msu.diazvald.rositasapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BagFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BagFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private String itemName;
    private float itemPrice;
    private List<String> ingredientList;
    private RecyclerView recyclerView;
    private List<Item> cartItemList = new ArrayList<>();


    private String description;
    private String imageUrl;

    private float totalPrice;

    private Button checkoutButton;

    private TextView totalPriceTextView;

    private BagViewModel bagViewModel;

    private CustomBaseAdapterBag customBaseAdapterBag;

    private double subTotal;

    private boolean isPickup = false;
    private RadioGroup radioGroup;



    public BagFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BagFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BagFragment newInstance(String param1, String param2) {
        BagFragment fragment = new BagFragment();
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
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bag, container, false);

        // bagViewModel to update the cart data when switching fragments
        bagViewModel = new ViewModelProvider(requireActivity()).get(BagViewModel.class);
        totalPriceTextView = (TextView) view.findViewById(R.id.total_price);
        checkoutButton = (Button) view.findViewById(R.id.checkout_button);
        radioGroup = (RadioGroup) view.findViewById(R.id.pickup_dine_in);


        // get arguments
        if (getArguments() != null && getArguments().getString("name") != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            itemName = getArguments().getString("name");
            if(getArguments().getString("price") != null){
                itemPrice = Float.parseFloat(getArguments().getString("price"));
            }
            if (getArguments().getStringArrayList("ingredients") != null){
                ingredientList = getArguments().getStringArrayList("ingredients");
            }
            if (getArguments().getString("description") != null){
                description = getArguments().getString("description");
            }
            if (getArguments().getString("imageUrl") != null){
                imageUrl = getArguments().getString("imageUrl");
            }

            // creates a new Item Object
            Item item = new Item();
            item.setPrice(String.valueOf(itemPrice));
            item.setName(itemName);
            item.setDescription(description);
            item.setImageUrl(imageUrl);
            item.setIngredients(ingredientList);
            bagViewModel.addItemToCart(item); // initializes cart
            cartItemList.add(item);
            totalPriceTextView.setText("Subtotal: $" + String.format("%.2f", itemPrice));
        }

        // recycler view for my cart
        recyclerView = view.findViewById(R.id.item_list);

        // creates custom base adapter to update price when quantity changes
        customBaseAdapterBag = new CustomBaseAdapterBag(getContext(), new ArrayList<>(), new CustomBaseAdapterBag.OnItemQuantityChangedListener() {
            @Override
            public void onItemQuantityChanged() {
                updateTotalPrice();
            }
        });
        //sets adapter
        recyclerView.setAdapter(customBaseAdapterBag);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // observer (listener) to see when cart items are added to update the cart items and price
        bagViewModel.getCartItems().observe(getViewLifecycleOwner(), new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> cartItems){
                customBaseAdapterBag.updateData(cartItems);
                updateTotalPrice();
            }
        });
        updateTotalPrice();

        // check **
        bagViewModel.getCartItems().observe(getViewLifecycleOwner(), items -> {customBaseAdapterBag.updateData(items); });

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // if no radiobutton selected toast return **

                Bundle args = new Bundle();

                args.putString("subTotal", String.valueOf(subTotal));

                if(radioGroup.getCheckedRadioButtonId() == R.id.radio_pickup){
                    isPickup = true;
                }
                args.putBoolean("isPickup", isPickup);

                CheckoutFragment checkoutFragment = new CheckoutFragment();
                checkoutFragment.setArguments(args);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.fragment_container, checkoutFragment);
                fragmentTransaction.addToBackStack(null);
                BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
                bottomNavigationView.setSelectedItemId(R.id.action_bag);

                fragmentTransaction.commit();
            }
            });


        // Inflate the layout for this fragment
        return view;
    }


    public void updateTotalPrice(){
        double subtotal = 0.00;
        Log.d("MyApp", "Item count: "+ cartItemList.size());
        List<Item> cartItems = bagViewModel.getCartItems().getValue();
        if(cartItems != null){
            cartItemList = cartItems;
        }
        for(Item items: cartItemList){
            Log.d("myapp", "Item name: "+ items.getName());
            subtotal += items.getTotalPrice();
        }
        subTotal = subtotal;
        Log.d("myapp", "Subtotal: $$" + subtotal);
        totalPriceTextView.setText("Subtotal: $" + String.format("%.2f", subtotal));

    }



}