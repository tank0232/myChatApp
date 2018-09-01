package com.example.ryan.mychatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button SaveChangesButton;
    private EditText StatusInput;

    private DatabaseReference changeStatusRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        changeStatusRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        loadingbar = new ProgressDialog(this);


        mToolbar = (Toolbar) findViewById(R.id.status_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SaveChangesButton = (Button) findViewById(R.id.save_status_change_button);
        StatusInput = (EditText) findViewById(R.id.status_input);
        String old_status = getIntent().getExtras().get("user_status").toString();
        StatusInput.setText(old_status);

        SaveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_status = StatusInput.getText().toString();
                ChangeProfileStatus(new_status);
            }
        });

    }

    private void ChangeProfileStatus(String new_status) {
        if(TextUtils.isEmpty(new_status))
        {
            Toast.makeText(StatusActivity.this,
                    "Please write your status",Toast.LENGTH_SHORT).show();
        }

        else
        {
            loadingbar.setTitle("Change Profile Status");
            loadingbar.setMessage(("Please wait, while we are updating your task..."));

          changeStatusRef.child("user_status").setValue(new_status)
                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                          if(task.isSuccessful())
                          {
                              loadingbar.dismiss();
                              Intent settingIntent = new Intent(StatusActivity.this,SettingActivity.class);
                              startActivity(settingIntent);

                              Toast.makeText(StatusActivity.this,
                                      "Profile Status Updated Successfully...",
                                      Toast.LENGTH_LONG).show();
                          }

                          else
                          {
                              Toast.makeText(StatusActivity.this,
                                      "Error occured...",
                                      Toast.LENGTH_LONG).show();
                          }
                      }
                  });
        }
    }
}
