package com.conestoga.weatherfits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateGenderActivity extends AppCompatActivity {

    EditText edtEmail, edtName;
    RadioGroup rgGender;
    RadioButton rbGenderSelected;
    Button btnResetProfile;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    String name, email, gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_gender);

        edtEmail = findViewById(R.id.edtEmail);
        edtName = findViewById(R.id.edtName);
        rgGender = findViewById(R.id.rgGender);
        rgGender.clearCheck();
        btnResetProfile = findViewById(R.id.btnResetProfile);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        edtEmail.setEnabled(false);

        showUserData(firebaseUser);

        btnResetProfile.setOnClickListener(view -> {

            int genderSelected = rgGender.getCheckedRadioButtonId();
            rbGenderSelected = findViewById(genderSelected);


            String gender = rbGenderSelected.getText().toString();
            UpdateGender(gender);
        });
    }

    private void showUserData(FirebaseUser firebaseUser) {

//        fetch data from database and display it
        String ID = firebaseUser.getUid();

//        extracting user reference from database for "users"
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails readUserDetails = snapshot.getValue(UserDetails.class);
                if (readUserDetails != null) {
                    name = readUserDetails.name;
                    gender = readUserDetails.gender;
                    email = firebaseUser.getEmail();

                    edtEmail.setText(email);
                    edtName.setText(name);

//                    show gender through radio button
                    if (gender.equals("Male")) {
                        rbGenderSelected = findViewById(R.id.rbMale);

                    } else {
                        rbGenderSelected = findViewById(R.id.rbFemale);
                    }
                    rbGenderSelected.setChecked(true);
                } else {
                    Toast.makeText(UpdateGenderActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateGenderActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void UpdateGender(String gender) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(UpdateGenderActivity.this, "Please enter your name", Toast.LENGTH_LONG).show();
            edtName.setError("Name is required");
            edtName.requestFocus();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(UpdateGenderActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
            edtEmail.setError("Email is required");
            edtEmail.requestFocus();

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(UpdateGenderActivity.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
            edtEmail.setError("Gender is required");
            edtEmail.requestFocus();

        } else if (TextUtils.isEmpty(rbGenderSelected.getText())) {
            Toast.makeText(UpdateGenderActivity.this, "Please select your gender", Toast.LENGTH_LONG).show();
            rbGenderSelected.setError("gender is required");
            rbGenderSelected.requestFocus();

        } else {

//            obtain data from user
            gender = rbGenderSelected.getText().toString();
            name = edtName.getText().toString();
            email = edtEmail.getText().toString();

//            enter user data in database
            UserDetails writeUserDetails = new UserDetails(name, email, gender);

//            extract user reference from database
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            String ID = firebaseUser.getUid();

            reference.child(ID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
//                        set new display name

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                        firebaseUser.updateProfile(profileUpdates);

                        Toast.makeText(UpdateGenderActivity.this, "Profile update successful", Toast.LENGTH_LONG).show();

//                        Stop user to return
                        Intent intent = new Intent(UpdateGenderActivity.this, AccountsActivity.class);

                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    }
}

