package com.conestoga.weatherfits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText edtResetEmail;
    Button btnResetPass;

    FirebaseAuth mAuth;
    final static String TAG = "ForgotPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        edtResetEmail=(EditText) findViewById(R.id.edtResetEmail);
        btnResetPass=(Button) findViewById(R.id.btnResetPass);


        btnResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtResetEmail.getText().toString();

                if (TextUtils.isEmpty(email))
                {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    edtResetEmail.setError("Email is required");
                    edtResetEmail.requestFocus();
                }else  if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter valid email", Toast.LENGTH_SHORT).show();
                    edtResetEmail.setError("Valid Email is required");
                    edtResetEmail.requestFocus();
                }else {
                    resetPassword(email);

                }
            }
        });
    }

    private void resetPassword(String email) {

        mAuth = FirebaseAuth.getInstance();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(ForgotPasswordActivity.this, "check mail for password reset mail", Toast.LENGTH_SHORT).show();
                   Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                }else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        Toast.makeText(ForgotPasswordActivity.this, "user does not exist", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(ForgotPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}