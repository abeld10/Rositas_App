package edu.msu.diazvald.rositasapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView listView;
    private List<Item> menuList; // fetch items from firestore and initialize this list
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private  CollectionReference menuItemsRef = db.collection("menuItems");

    public MenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuFragment newInstance(String param1, String param2) {
        MenuFragment fragment = new MenuFragment();
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

        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        initializeList();
        return view;
    }

    public void initializeList(){
        menuList = new ArrayList<>();

        // adds a complete listener for getting the menu items
        menuItemsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    // if successful for each menu item in the firestore database collection
                    // "menuItems", create a new item for each and set the name, description
                    // price and image url. Add each item to a list of items
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Item item = document.toObject(Item.class);
                        if(document.contains("name")){
                            item.setName(document.getString("name"));
                        }
                        if(document.contains("description")){
                            item.setDescription(document.getString("description"));
                        }
                        if(document.contains("price")){
                            item.setPrice(document.getString("price"));
                        }
                        if(document.contains("imageUrl")){
                            item.setImageUrl(document.getString("imageUrl"));
                        }

                        menuList.add(item);
                    }
                    // sets adapter for listView
                    Context context = getContext();
                    if (context != null) {
                        CustomBaseAdapter customBaseAdapter = new CustomBaseAdapter(context, menuList);
                        listView.setAdapter(customBaseAdapter);
                    }
                    else {
                        if(getActivity() != null) {
                            Toast.makeText(getActivity(), "Error context is null", Toast.LENGTH_LONG).show();
                        }
                    }
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id){

                            Log.i("CUSTOM_LIST_VIEW", "Item Clicked: " + position);
                        }
                    });

                } else {
                    Log.d("CUSTOM_LIST_VIEW", "Error getting documents: ", task.getException());
                }
            }
        });
    }







}