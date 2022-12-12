package com.conestoga.weatherfits;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class FitsActivity extends AppCompatActivity {

    double temp, feelsLike;
    String city, styleSelected;
    boolean isRain;

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


    }
}