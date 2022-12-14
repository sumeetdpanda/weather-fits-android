package com.conestoga.weatherfits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.conestoga.weatherfits.adapter.ListAdapter;
import com.conestoga.weatherfits.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ListFavouritesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference mref;
    ListAdapter myAdapter;
    ArrayList<Product> list;
    StorageReference root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_favourites);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        list = new ArrayList<>();
        recyclerView = findViewById(R.id.rvFav);
        mref= FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("favs");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //adding firebase recyclerAdapter

        list = new ArrayList<>();
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()){
                    String name = (String) snap.child("name").getValue();
                    String link = (String) snap.child("link").getValue();
                    String image = (String) snap.child("image").getValue();

                    list.add(new Product(name, link, image));
                    System.out.println("LOREM_IPSUM LIST " + snap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        new Handler().postDelayed(() -> {
            System.out.println("LOREM_IPSUM DATA: " + list.get(0).getName());
            myAdapter= new ListAdapter(ListFavouritesActivity.this, list);
            recyclerView.setAdapter(myAdapter);
        }, 2000);

    }
}