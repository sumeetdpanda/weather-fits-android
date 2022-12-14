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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.conestoga.weatherfits.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    EditText edtEmail, edtPassword, edtName, edtConfirm;
    Button btnCancel, btnSignUp;
    TextView btnLogin;
    RadioGroup rgGender;
    RadioButton rbGenderSelected;

    Intent intent;
    static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtName = (EditText) findViewById(R.id.edtName);
        edtConfirm = (EditText) findViewById(R.id.btnForgot);
        rgGender = (RadioGroup) findViewById(R.id.rgGender);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnLogin = (TextView) findViewById(R.id.btnSignup1);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        rgGender.clearCheck();

        //keep all the radio buttons unchecked

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int genderSelected = rgGender.getCheckedRadioButtonId();
                rbGenderSelected = findViewById(genderSelected);

                //Get entered data
                String name = edtName.getText().toString();
                String email = edtEmail.getText().toString();
                String pass = edtPassword.getText().toString();
                String confirm = edtConfirm.getText().toString();
                String gender;

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your name", Toast.LENGTH_LONG).show();
                    edtName.setError("Name is required");
                    edtName.requestFocus();
                } else if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    edtEmail.setError("Email is required");
                    edtEmail.requestFocus();

                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(SignUpActivity.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                    edtEmail.setError("Valid Email is required");
                    edtEmail.requestFocus();

                } else if (rgGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(SignUpActivity.this, "Please select your gender", Toast.LENGTH_LONG).show();
                    rbGenderSelected.setError("gender is required");
                    rbGenderSelected.requestFocus();

                } else if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(SignUpActivity.this, "Please enter password", Toast.LENGTH_LONG).show();
                    edtPassword.setError("password is required");
                    edtPassword.requestFocus();

                } else if (TextUtils.isEmpty(confirm)) {
                    Toast.makeText(SignUpActivity.this, "Please Re-enter password", Toast.LENGTH_LONG).show();
                    edtConfirm.setError("password confirmation is required");
                    edtConfirm.requestFocus();

                } else if (!pass.equals(confirm)) {
                    Toast.makeText(SignUpActivity.this, "Password do not match ", Toast.LENGTH_LONG).show();
                    edtConfirm.setError("password confirmation is required");
                    edtConfirm.requestFocus();

                    //clear the entered password
                    edtPassword.clearComposingText();
                    edtConfirm.clearComposingText();
                } else {
                    gender = rbGenderSelected.getText().toString();
                    registerUser(name, email, pass, confirm, gender);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    //register user using the data entered
    private void registerUser(String name, String email, String pass, String confirm, String gender) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //create user
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser firebaseUser = mAuth.getCurrentUser();


                    //Enter user data into firebase database.
                    UserDetails writeUserDetails = new UserDetails(name, email, gender);


                    //Extract user reference from database for registered user

                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users");
                    myRef.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {


                                //send verification email

                                firebaseUser.sendEmailVerification();


                                Toast.makeText(SignUpActivity.this, "User registered successfully", Toast.LENGTH_LONG).show();

                                //Open home page
                                intent = new Intent(SignUpActivity.this, MainActivity.class);

                                //User cannot move back once registered
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);

                                //close registration activity
                                finish();
                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    edtPassword.setError("Your password is weak..please choose long passwords");
                                    edtPassword.requestFocus();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    edtPassword.setError("Your email is invalid or already in use...kindly re-enter your email");
                                    edtPassword.requestFocus();
                                } catch (FirebaseAuthUserCollisionException e) {
                                    edtPassword.setError("User is already registered with this email.Use another email");
                                    edtPassword.requestFocus();
                                } catch (Exception e) {
                                    Log.e(TAG, e.getMessage());
                                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}