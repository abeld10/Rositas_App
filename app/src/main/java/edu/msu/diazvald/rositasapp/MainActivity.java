package edu.msu.diazvald.rositasapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance(); // Initialize Firebase Authenticator

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener); // navListener function for Bottom Nav Bar

        // Using fragment container for the view
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
    }

    // listener for bottom nav view
    private BottomNavigationView.OnItemSelectedListener navListener =
            (NavigationBarView.OnItemSelectedListener) item -> {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.action_menu:
                        selectedFragment = new MenuFragment();
                        break;
                    case R.id.action_bag:
                        selectedFragment = new BagFragment();
                        break;
                    case R.id.action_account:
                        selectedFragment = onAccount(); // OnAccount function returns fragment
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit(); // changes fragment
                return true;
            };


    /**
     * onAccount function returns fragment
     * according to user status (logged in)
     * @return fragment
     */
    public Fragment onAccount() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){ //if user is logged in
            return new AccountFragment();
        }
        else{
            return new AccountGuestFragment();
        }
    }
}