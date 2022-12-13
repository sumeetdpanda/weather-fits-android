package com.conestoga.weatherfits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
    String city, styleSelected, name, link, image, weather;
    boolean isRain;

    RecyclerView recyclerView;

    List<Product> listProducts = new ArrayList<>();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fits);

        Intent intent = getIntent();
        temp = intent.getExtras().getDouble("TEMP");
        feelsLike = intent.getExtras().getDouble("FEELS_LIKE");
        city = intent.getExtras().getString("CITY");
        isRain = intent.getExtras().getBoolean("IS_RAIN");
        styleSelected = intent.getExtras().getString("STYLE");

        recyclerView = findViewById(R.id.recyclerView);

        if(temp >= 15){
            weather = "Summer";
        } else if( temp < 15  && temp >= 3){
            weather = "Fall";
        } else {
            weather = "Winter";
        }

//        if(user != null){
//
//                } else {
//                    Toast.makeText(FitsActivity.this, "You need to signup to get Fits", Toast.LENGTH_LONG).show();
//                }

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(styleSelected).child("Female").child(weather);
        mRef.addValueEventListener(new ValueEventListener() {
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO: Add progress
                ProductAdapter adapter = new ProductAdapter(FitsActivity.this, listProducts);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FitsActivity.this, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adapter);
            }
        }, 2000);
    }
}