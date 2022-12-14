package com.conestoga.weatherfits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountsActivity extends AppCompatActivity {
    TextView txtWelcome, txtEmail, txtGender, txtPass, btnFav;

    String name, gender, email;
    FirebaseAuth mAuth;

    BottomNavigationView bottomNav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        txtWelcome = (TextView) findViewById(R.id.txtWelcome);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtGender = (TextView) findViewById(R.id.txtGender);
        txtPass = (TextView) findViewById(R.id.txtPass);
        btnFav = (TextView) findViewById(R.id.btnFav);
        bottomNav = findViewById(R.id.bottomNav);

        setBottomNavView();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(AccountsActivity.this, "Something went wrong.....User's details are not available", Toast.LENGTH_LONG).show();

        } else {
            showUserProfile(firebaseUser);
        }
    }

    private void setBottomNavView() {
        bottomNav.setSelectedItemId(R.id.nav_profile);
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        startActivity(new Intent(AccountsActivity.this, MapsActivity.class));
                        break;
                    case R.id.nav_about:
                        startActivity(new Intent(AccountsActivity.this, AboutActivity.class));
                        break;
                    case R.id.nav_profile:
                        break;
                }
                return false;
            }
        });
    }

    private void showUserProfile(FirebaseUser firebaseUser) {

        String userID = firebaseUser.getUid();


        //Extracting user reference from Database for "registered users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails readUserDetails = snapshot.getValue(UserDetails.class);

                if (readUserDetails != null) {
                    name = readUserDetails.name;
                    gender = readUserDetails.gender;
                    email = readUserDetails.email;
                    txtWelcome.setText("Welcome " + name + "!");
                    txtEmail.setText(email);
                    txtGender.setText(gender);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(AccountsActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();

            }
        });

        txtEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountsActivity.this, UpdateEmailActivity.class);
                startActivity(intent);

            }
        });

        txtGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AccountsActivity.this, UpdateGenderActivity.class);
                startActivity(intent);


            }
        });

        btnFav.setOnClickListener(view -> {

            Intent intent = new Intent(AccountsActivity.this, ListFavouritesActivity.class);
            startActivity(intent);

        });


        txtPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountsActivity.this, UpdatePasswordActivity.class);
                startActivity(intent);

            }
        });


        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountsActivity.this, ListFavouritesActivity.class);
                startActivity(intent);


            }
        });
    }
}

