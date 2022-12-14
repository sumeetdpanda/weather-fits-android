package com.conestoga.weatherfits;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.jar.Attributes;

public class HomeActivity extends AppCompatActivity {
    Button btnOff,btnAcc;
    Intent intent;
    GoogleSignInOptions googleSignInOptions;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnAcc=(Button) findViewById(R.id.btnAcc);
        btnOff=(Button) findViewById(R.id.btnSignOut);


        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(HomeActivity.this, LoginActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
        });

        btnAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(HomeActivity.this, AccountsActivity.class);
                startActivity(intent);


            }
        });
    }
}