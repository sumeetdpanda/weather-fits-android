package com.conestoga.weatherfits;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class WearTypeActivity extends AppCompatActivity {

    double temp, feelsLike;
    String city, styleSelected;
    boolean isRain;

    Button btnStreet, btnFormal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear_type);

        Intent intent = getIntent();
        temp = intent.getExtras().getDouble("TEMP");
        feelsLike = intent.getExtras().getDouble("FEELS_LIKE");
        city = intent.getExtras().getString("CITY");
        isRain = intent.getExtras().getBoolean("IS_RAIN");

        btnStreet = findViewById(R.id.btnStreet);
        btnFormal = findViewById(R.id.btnFormal);

        btnStreet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                styleSelected = "Street";
                startIntent();
            }
        });

        btnFormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                styleSelected = "Formal";
                startIntent();
            }
        });
    }

    private void startIntent(){
        Intent intent = new Intent(WearTypeActivity.this, FitsActivity.class);
        intent.putExtra("TEMP", temp);
        intent.putExtra("FEELS_LIKE", feelsLike);
        intent.putExtra("IS_RAIN", isRain);
        intent.putExtra("CITY", city);
        intent.putExtra("STYLE", styleSelected);
        startActivity(intent);
    }
}