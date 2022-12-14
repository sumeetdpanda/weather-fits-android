package com.conestoga.weatherfits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class AboutActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        bottomNav = findViewById(R.id.bottomNav);
        setBottomNavView();
    }

    private void setBottomNavView() {
        bottomNav.setSelectedItemId(R.id.nav_about);
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        startActivity(new Intent(AboutActivity.this, MapsActivity.class));
                        break;
                    case R.id.nav_about:
                        break;
                    case R.id.nav_profile:
                        startActivity(new Intent(AboutActivity.this, AccountsActivity.class));
                        break;
                }
                return false;
            }
        });
    }
}