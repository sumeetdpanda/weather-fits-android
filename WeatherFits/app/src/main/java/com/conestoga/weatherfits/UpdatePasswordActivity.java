package com.conestoga.weatherfits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UpdatePasswordActivity extends AppCompatActivity {

    EditText edtNewpass,edtConfirm,edtPass;
    Button btnResetPass,btnAuthenticate;
    RelativeLayout RL_Pass,RL_Reset_Pass;
    TextView txtDisplay;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    String oldPass,newPass,userPassConfirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);


        edtNewpass=(EditText) findViewById(R.id.edtnewPass);
        edtConfirm=(EditText) findViewById(R.id.edtConfirm);
        edtPass=(EditText) findViewById(R.id.edtPass);
        btnResetPass=(Button) findViewById(R.id.btnResetPass);
        btnAuthenticate=(Button) findViewById(R.id.btnAuthenticate);
        RL_Pass=(RelativeLayout) findViewById(R.id.Rl_Pass);
        RL_Reset_Pass=(RelativeLayout) findViewById(R.id.Rl_Reset_Pass);
        txtDisplay=(TextView) findViewById(R.id.txtDisplay);


        btnResetPass.setEnabled(false);
        edtNewpass.setEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser == null)
        {
            Toast.makeText(UpdatePasswordActivity.this,"Something went wrong",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(UpdatePasswordActivity.this,AccountsActivity.class);
            startActivity(intent);
            finish();
        }else {
            reAuthenticate(firebaseUser);
        }
    }

    private void reAuthenticate(FirebaseUser firebaseUser) {

        btnAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                oldPass = edtPass.getText().toString();

                if (TextUtils.isEmpty(oldPass)){
                    Toast.makeText(UpdatePasswordActivity.this,"password is needed to continue",Toast.LENGTH_LONG).show();
                    edtPass.setError("Please enter your password for authentication");
                    edtPass.requestFocus();


                }else {
                    AuthCredential credential= EmailAuthProvider.getCredential(firebaseUser.getEmail(), oldPass);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                Toast.makeText(UpdatePasswordActivity.this,"Password is verified"+
                                        "You can  update password now",Toast.LENGTH_LONG).show();

                                txtDisplay.setText("You are authenticated...you can update email now");
                                //disable edittext for password and enable edit text for new email and update email

                                edtNewpass.setEnabled(true);
                                edtPass.setEnabled(false);
                                btnResetPass.setEnabled(true);
                                btnAuthenticate.setEnabled(false);


                                //Change the colour of button update email

                                btnResetPass.setBackgroundTintList(ContextCompat.getColorStateList(UpdatePasswordActivity.this,R.color.dark_green));


                                btnResetPass.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        newPass = edtNewpass.getText().toString();

                                        changePassword(firebaseUser);
                                    }
                                });



                            }else {
                                try {
                                    throw task.getException();
                                }catch (Exception e)
                                {
                                    Toast.makeText(UpdatePasswordActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                }

                            }
                        }
                    });
                }

            }
        });
    }

    private void changePassword(FirebaseUser firebaseUser) {


        String newPass = edtNewpass.getText().toString();
        String confirm = edtConfirm.getText().toString();

        if (TextUtils.isEmpty(newPass)) {
            Toast.makeText(UpdatePasswordActivity.this,"Enter new Password",Toast.LENGTH_LONG).show();
            edtNewpass.setError("Please enter new password");
            edtNewpass.requestFocus();
        }else  if (TextUtils.isEmpty(confirm)) {
            Toast.makeText(UpdatePasswordActivity.this,"Please confirm new Password",Toast.LENGTH_LONG).show();
            edtConfirm.setError("Please confirm your password");
            edtConfirm.requestFocus();
    }else if (!newPass.matches(confirm)) {
            Toast.makeText(UpdatePasswordActivity.this,"Password did not match",Toast.LENGTH_LONG).show();
            edtConfirm.setError("Please re-enter same password");
            edtConfirm.requestFocus();
        }else if (oldPass.matches(newPass)) {
            Toast.makeText(UpdatePasswordActivity.this,"Old password cannot be same as new password",Toast.LENGTH_LONG).show();
            edtNewpass.setError("Please enter different password");
            edtNewpass.requestFocus();
        }else {
            firebaseUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
               if (task.isSuccessful()) {
                   Toast.makeText(UpdatePasswordActivity.this,"Password has been changed",Toast.LENGTH_LONG).show();
                   Intent intent = new Intent(UpdatePasswordActivity.this,AccountsActivity.class);
                   startActivity(intent);
                   finish();
               }else {
                   try {

                           throw task.getException();

                   } catch (Exception e) {
                       Toast.makeText(UpdatePasswordActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                   }
               }
                }
            });
        }

        }


}