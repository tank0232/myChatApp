package com.example.ryan.mychatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;


public class RegisterActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private EditText RegisterName;
    private EditText RegisterEmail;
    private EditText RegisterPassword;
    private Button CreateAccountButton;
    private FirebaseAuth mAuth;
    private DatabaseReference storeUserDefaultDataReference;


    private ProgressDialog loadingbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RegisterName = (EditText) findViewById(R.id.register_name);
        RegisterEmail = (EditText) findViewById(R.id.register_email);
        RegisterPassword = (EditText) findViewById(R.id.register_password);
        CreateAccountButton = (Button) findViewById(R.id.create_account_btn);
        loadingbar = new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = RegisterName.getText().toString();
                String email = RegisterEmail.getText().toString();
                String password = RegisterPassword.getText().toString();
                RegisterAccount(name, email, password);
            }
        });
    }

    private void RegisterAccount(final String name, String email, String password) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(RegisterActivity.this, "Please write your freaking name.", Toast.LENGTH_LONG).show();

        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, "Please write your email.", Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty((password))) {
            Toast.makeText(RegisterActivity.this, "Password cannot be empty", Toast.LENGTH_LONG).show();
        } else {

            loadingbar.setTitle("Creating new account");
            loadingbar.setMessage("Please wait, we are register new account");
            loadingbar.show();


            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful())
                       {

                           String DeviceToken = FirebaseInstanceId.getInstance().getToken();
                           String current_user_Id = mAuth.getCurrentUser().getUid();
                           storeUserDefaultDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_Id);
                           storeUserDefaultDataReference.child("user_name").setValue(name);
                           storeUserDefaultDataReference.child("user_status").setValue("Hey there, I am "+name+", using mychat");
                           storeUserDefaultDataReference.child("user_image").setValue("default_profile");
                           storeUserDefaultDataReference.child("device_token").setValue(DeviceToken);
                           storeUserDefaultDataReference.child("user_thumb_image").setValue("default_image")
                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                          if(task.isSuccessful())
                                          {
                                              Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                                              mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                              startActivity(mainIntent);
                                              finish();
                                          }
                                       }
                                   });




                       }
                       else
                       {
                           Toast.makeText(RegisterActivity.this,"Error Occur, please try again",Toast.LENGTH_SHORT).show();
                       }
                       loadingbar.dismiss();
                }
            });
        }
    }
}