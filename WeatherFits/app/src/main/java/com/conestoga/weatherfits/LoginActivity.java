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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail,edtPassword;
    Button btnSignUp,btnCancel,btnForgot;
    ImageView imgGoogle;
    TextView btnSignUp1;

    Intent intent;

    FirebaseAuth authProfile;
    private  static final String Tag = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword=(EditText) findViewById(R.id.edtPassword);
        btnSignUp=(Button) findViewById(R.id.btnSignUp);
        btnCancel=(Button) findViewById(R.id.btnCancel);
        btnSignUp1=(TextView) findViewById(R.id.btnSignup1);
        btnForgot=(Button) findViewById(R.id.btnForgot);

        authProfile= FirebaseAuth.getInstance();




        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this,"You can reset your password now",Toast.LENGTH_LONG).show();
               startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });



        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String email=edtEmail.getText().toString();
                String pass=edtPassword.getText().toString();

                if (TextUtils.isEmpty(email))
                {
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    edtEmail.setError("Email is required");
                    edtEmail.requestFocus();

                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(LoginActivity.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                    edtEmail.setError("Valid Email is required");
                    edtEmail.requestFocus();
                }else if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(LoginActivity.this, "Please enter password", Toast.LENGTH_LONG).show();
                    edtPassword.setError("password is required");
                    edtPassword.requestFocus();
                }
                else
                {
                    loginUser(email,pass);
                }



            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);

            }
        });

        btnSignUp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);

            }
        });


    }

 private void loginUser(String email, String pass) {

        authProfile.signInWithEmailAndPassword(email,pass).addOnCompleteListener(LoginActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    intent = new Intent(LoginActivity.this,MapsActivity.class);
                    startActivity(intent);
                }else {
                    try {
                        {
                            throw task.getException();
                        }
                    } catch (FirebaseAuthInvalidUserException e) {
                        edtEmail.setError("user does not exist.....Please register again");
                        edtEmail.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        edtEmail.setError("Invalid credentials......please check and login again");
                        edtEmail.requestFocus();
                    } catch (Exception e) {
                        Log.e(Tag,e.getMessage());
                        Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }


                }
            }
        });
    }

    //Check if user is logged in or not

    @Override
    protected void onStart() {
        super.onStart();
        if (authProfile.getCurrentUser() != null)
        {
            intent = new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }


}