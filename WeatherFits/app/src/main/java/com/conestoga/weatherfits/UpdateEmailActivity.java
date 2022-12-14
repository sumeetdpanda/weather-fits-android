package com.conestoga.weatherfits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UpdateEmailActivity extends AppCompatActivity {

    EditText edtNewEmail, edtOldEmail, edtPass;
    Button btnResetEmail, btnAuthenticate;
    RelativeLayout RL_Email, RL_Reset_Email;
    TextView txtDisplay;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    String oldEmail, newEmail, userPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

        edtNewEmail = (EditText) findViewById(R.id.edtNewEmail);
        edtOldEmail = (EditText) findViewById(R.id.edtOldEmail);
        edtPass = (EditText) findViewById(R.id.edtPass);
        btnResetEmail = (Button) findViewById(R.id.btnResetEmail);
        btnAuthenticate = (Button) findViewById(R.id.btnAuthenticate);
        RL_Email = (RelativeLayout) findViewById(R.id.Rl_Email);
        RL_Reset_Email = (RelativeLayout) findViewById(R.id.Rl_Reset_Email);
        txtDisplay = (TextView) findViewById(R.id.txtDisplay);

        btnResetEmail.setEnabled(false);
        edtNewEmail.setEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        //set old  email on text view

        oldEmail = firebaseUser.getEmail();
        edtOldEmail.setText(oldEmail);

        if (firebaseUser != null) {
            Toast.makeText(UpdateEmailActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
        } else {
            reAuthenticate(firebaseUser);
        }


    }

    //    reauthenticate user before updating email
    private void reAuthenticate(FirebaseUser firebaseUser) {
        btnAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //obtain password for authentication

                userPass = edtPass.getText().toString();

                if (TextUtils.isEmpty(userPass)) {
                    Toast.makeText(UpdateEmailActivity.this, "password is needed to continue", Toast.LENGTH_LONG).show();
                    edtPass.setError("Plese enter your password for authentication");
                    edtPass.requestFocus();
                } else {
                    //authenticate use

                    AuthCredential credential = EmailAuthProvider.getCredential(oldEmail, userPass);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UpdateEmailActivity.this, "Password is verified" +
                                        "You can  update email now", Toast.LENGTH_LONG).show();

                                txtDisplay.setText("You are authenticated...you can update email now");
                                //disable edittext for password and enable edit text for new email and update email

                                edtNewEmail.setEnabled(true);
                                edtPass.setEnabled(false);
                                btnResetEmail.setEnabled(true);
                                btnAuthenticate.setEnabled(false);

                                //Change the colour of button update email

                                btnResetEmail.setBackgroundTintList(ContextCompat.getColorStateList(UpdateEmailActivity.this, R.color.dark_green));

                                btnResetEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        newEmail = edtNewEmail.getText().toString();

                                        if (TextUtils.isEmpty(newEmail)) {

                                            Toast.makeText(UpdateEmailActivity.this, "Enter new email", Toast.LENGTH_LONG).show();
                                            edtNewEmail.setError("Please enter email");
                                            edtNewEmail.requestFocus();
                                        } else if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                                            Toast.makeText(UpdateEmailActivity.this, "Enter valid email", Toast.LENGTH_LONG).show();
                                            edtNewEmail.setError("Please enter valid email");
                                            edtNewEmail.requestFocus();

                                        } else if (oldEmail.matches(newEmail)) {
                                            Toast.makeText(UpdateEmailActivity.this, "New email cannot be same as old email", Toast.LENGTH_LONG).show();
                                            edtNewEmail.setError("Please enter different email");
                                            edtNewEmail.requestFocus();
                                        } else {
                                            updateEmail(firebaseUser);
                                        }
                                    }
                                });

                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }

                            }
                        }
                    });
                }
            }
        });


    }

    private void updateEmail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    //verify email

                    firebaseUser.sendEmailVerification();

                    Toast.makeText(UpdateEmailActivity.this, "Email has been updated....please verify your email", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(UpdateEmailActivity.this, AccountsActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


    }


}