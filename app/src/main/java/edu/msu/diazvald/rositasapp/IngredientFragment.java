package edu.msu.diazvald.rositasapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IngredientFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IngredientFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String itemName;

    private String imageUrl;
    private String description;

    private List<String> ingredientList;

    private RadioGroup meatRadioGroup;

    private CheckBox toppingCheckBox;

    private Button addToCartButton;

    private Map<String, CheckBox> toppingCheckboxes = new HashMap<>(); // Map of ingredient name and checkbox

    private Map<String, Float> meatPrices = new HashMap<>(); // Map for meat and prices

    private String lastSelectedMeat = null;
    float lastSelectedPrice = 0.00f;

    private TextView itemNameText;

    private TextView itemPriceText;

    private String priceString;

    private int quantity;

    private float itemPrice = 0;

    public IngredientFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IngredientFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IngredientFragment newInstance(String param1, String param2) {
        IngredientFragment fragment = new IngredientFragment();
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

        if (getArguments() != null) {
            itemName = getArguments().getString("name");
            priceString = getArguments().getString("price");
            imageUrl = getArguments().getString("imageUrl");
            description = getArguments().getString("description");
            quantity = getArguments().getInt("quantity");
            if (priceString != null){
                itemPrice = Float.parseFloat(priceString);
            }}

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredient, container, false);
        ingredientList = new ArrayList<>();
        meatRadioGroup = (RadioGroup) view.findViewById(R.id.meat_group);
        addToCartButton = (Button) view.findViewById(R.id.add_to_cart);

        initializeMeat(); // Prices for each meat in Map
        handleMeat();
        initializeToppings(view);
        handleToppings();

        itemNameText = (TextView) view.findViewById(R.id.ingredient_title);
        itemNameText.setText(itemName);
        itemPriceText = (TextView) view.findViewById(R.id.price_title);
        itemPriceText.setText(String.format("%.2f", itemPrice));

        // Listener for addToCartButton
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // loops through the map of ingredients and its checkboxes
                // ands adds checked boxes ingredient names to a list
                for (Map.Entry<String, CheckBox> entry : toppingCheckboxes.entrySet()) {
                    CheckBox checkbox = entry.getValue();
                    String topping = entry.getKey();
                    boolean isChecked = checkbox.isChecked();
                    if (isChecked) {
                        ingredientList.add(topping);
                    }
                }
                Bundle args = new Bundle();
                args.putString("name", itemName);
                args.putString("price", String.format("%.2f", itemPrice));
                args.putString("imageUrl", imageUrl);
                args.putString("description", description);
                args.putStringArrayList("ingredients", (ArrayList<String>) ingredientList);
                args.putInt("quantity", quantity);

                BagFragment bagFragment = new BagFragment();
                bagFragment.setArguments(args);

                // changes fragment
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.fragment_container, bagFragment);
                fragmentTransaction.addToBackStack(null);
                BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
                bottomNavigationView.setSelectedItemId(R.id.action_bag);


                fragmentTransaction.commit();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }


    /**
     * map of ingredients and checkboxes
     * to easily get to see which checkboxes and the name of it with ids
     * @param view
     */
    public void initializeToppings(View view) {
        toppingCheckboxes.put("cilantro", (CheckBox) view.findViewById(R.id.topping_cilantro));
        toppingCheckboxes.put("cebolla", (CheckBox) view.findViewById(R.id.topping_cebolla));
        toppingCheckboxes.put("tomate", (CheckBox) view.findViewById(R.id.topping_tomate));
        toppingCheckboxes.put("lechuga", (CheckBox) view.findViewById(R.id.topping_lechuga));
        toppingCheckboxes.put("aguacate", (CheckBox) view.findViewById(R.id.topping_aguacate));
        toppingCheckboxes.put("crema", (CheckBox) view.findViewById(R.id.topping_sourcream));
        toppingCheckboxes.put("queso", (CheckBox) view.findViewById(R.id.topping_queso));
        toppingCheckboxes.put("cebollayjalapeno", (CheckBox) view.findViewById(R.id.topping_cebyjal));
    }



    public void handleToppings() {
        for (Map.Entry<String, CheckBox> entry : toppingCheckboxes.entrySet()) {
            CheckBox checkbox = entry.getValue();
            // OnClickListener for each checkbox
            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Here, you can access the topping and whether it's checked or not
                    String topping = entry.getKey();
                    boolean isChecked = checkbox.isChecked();
                    // if checked add price
                    if (isChecked) {
                        if(topping.equals("cilantro") || topping.equals("cebolla") ||
                                topping.equals("tomate") || topping.equals("lechuga")){
                            itemPrice += 0.25;
                        }
                        if(topping.equals("queso")){
                            itemPrice += 0.50;
                        }
                        if(topping.equals("aguacate")){
                            itemPrice += 1.50;
                        }
                        if(topping.equals("crema")){
                            itemPrice += 0.75;
                        }
                        if(topping.equals("cebollayjalapeno")){
                            itemPrice += 2.50;
                        }

                    }else { // if clicked but unchecked then remove price
                        if(topping.equals("cilantro") || topping.equals("cebolla") ||
                                topping.equals("tomate") || topping.equals("lechuga")){
                            itemPrice -= 0.25;
                        }
                        if(topping.equals("queso")){
                            itemPrice -= 0.50;
                        }
                        if(topping.equals("aguacate")){
                            itemPrice -= 1.50;
                        }
                        if(topping.equals("crema")){
                            itemPrice -= 0.75;
                        }
                        if(topping.equals("cebollayjalapeno")){
                            itemPrice -= 2.50;
                        }
                    }
                    itemPriceText.setText(String.format("%.2f", itemPrice)); // update UI
                }
            });
        }
    }


    private void initializeMeat() {
        meatPrices.put("Asada", 0.00f);
        meatPrices.put("Al Pastor", 0.00f);
        meatPrices.put("Pollo", 0.00f);
        meatPrices.put("Chorizo", 0.00f);
        meatPrices.put("Barbacoa", 0.00f);
        meatPrices.put("Lengua", 1.00f);
        meatPrices.put("Cabeza", 0.00f);
        meatPrices.put("Tripa", 1.00f);
        meatPrices.put("Chicharron", 0.00f);
        meatPrices.put("Ground Beef", 0.00f);
        meatPrices.put("None", 0.00f);

    }

    public void handleMeat(){
        // Listener if changed meat option
        meatRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String selectedMeat = null;
                float selectedPrice = 0.00f;

                switch(checkedId){
                    case R.id.meat_asada:
                        selectedMeat = "Asada";
                        break;
                    case R.id.meat_alpastor:
                        selectedMeat = "Al Pastor";
                        break;
                    case R.id.meat_Pollo:
                        selectedMeat = "Pollo";
                        break;
                    case R.id.meat_chorizo:
                        selectedMeat = "Chorizo";
                        break;
                    case R.id.meat_barbacoa:
                        selectedMeat = "Barbacoa";
                        break;
                    case R.id.meat_lengua:
                        selectedMeat = "Lengua";
                        break;
                    case R.id.meat_chicharron:
                        selectedMeat = "Chicharron";
                        break;
                    case R.id.meat_groundbeef:
                        selectedMeat = "Ground Beef";
                        break;
                    case R.id.meat_tripa:
                        selectedMeat = "Tripa";
                        break;
                    case R.id.meat_cabeza:
                        selectedMeat = "Cabeza";
                        break;
                    case R.id.meat_none:
                        selectedMeat = "None";
                        break;

                }
                if (selectedMeat != null) {
                    selectedPrice = meatPrices.get(selectedMeat); // get price from map
                    // If there was a previously selected meat, remove its price from itemPrice
                    if (lastSelectedMeat != null) {
                        ingredientList.remove(lastSelectedMeat);
                        itemPrice -= lastSelectedPrice;
                    }
                    // Add the new meat and its price
                    ingredientList.add(selectedMeat);
                    itemPrice += selectedPrice;
                    // Update the last selected meat and price
                    lastSelectedMeat = selectedMeat;
                    lastSelectedPrice = selectedPrice;
                    itemPriceText.setText(String.format("%.2f", itemPrice));
                }

            }
        });


    }




}