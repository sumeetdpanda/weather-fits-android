package com.conestoga.weatherfits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.conestoga.weatherfits.adapter.ProductAdapter;
import com.conestoga.weatherfits.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FitsActivity extends AppCompatActivity {

    double temp, feelsLike;
    String city, styleSelected, name, link, image, weather, gender;
    boolean isRain;

    RecyclerView recyclerView;
    ProgressBar progressBar;

    List<Product> listProducts = new ArrayList<>();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fits);

//        Getting Intent Values
        Intent intent = getIntent();
        temp = intent.getExtras().getDouble("TEMP");
        feelsLike = intent.getExtras().getDouble("FEELS_LIKE");
        city = intent.getExtras().getString("CITY");
        isRain = intent.getExtras().getBoolean("IS_RAIN");
        styleSelected = intent.getExtras().getString("STYLE");

//        Instantiating Views
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

//        Logic for Fit Suggestion
        if(temp >= 15){
            weather = "Summer";
        } else if( temp < 15  && temp >= 3){
            weather = "Fall";
        } else {
            weather = "Winter";
        }

//        Check if User is signed in then get data from database
        if(user != null){
            mRef.child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    System.out.println("LOREM_IPSUM DATA: " + snapshot.child("gender").getValue());
                    gender = (String) snapshot.child("gender").getValue();
                    DatabaseReference mRef1 = FirebaseDatabase.getInstance().getReference(styleSelected).child(gender).child(weather);
                    mRef1.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot snap: snapshot.getChildren()){
                                name = snap.child("Name").getValue().toString();
                                link = snap.child("Link").getValue().toString();
                                image = snap.child("Image").getValue().toString();
                                listProducts.add(new Product(name, link, image));
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            System.out.println("The read failed: " + error.getMessage());
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            Toast.makeText(FitsActivity.this, "You need to signup to get Fits", Toast.LENGTH_LONG).show();
        }

//        Adding Delay for 2 secs so we can get data from DB
        new Handler().postDelayed(() -> {
            // TODO: Add progress
            ProductAdapter adapter = new ProductAdapter(FitsActivity.this, listProducts, user);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FitsActivity.this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(adapter);

            runOnUiThread(() -> progressBar.setVisibility(View.GONE));
        }, 2000);
    }
}