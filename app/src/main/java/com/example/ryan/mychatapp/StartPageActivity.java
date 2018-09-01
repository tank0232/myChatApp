package com.example.ryan.mychatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartPageActivity extends AppCompatActivity {

    private Button NeedNewAccountButton;
    private Button AlreadyHaveAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        NeedNewAccountButton = (Button) findViewById(R.id.need_new_account_btn);
        AlreadyHaveAccountButton = (Button) findViewById(R.id.already_have_account_btn);

        NeedNewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newaccountIntent = new Intent(StartPageActivity.this,RegisterActivity.class);
                startActivity(newaccountIntent);
            }
        });

        AlreadyHaveAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent oldAccountIntent = new Intent(StartPageActivity.this,LoginActivity.class);
                startActivity(oldAccountIntent);
            }
        });
    }
}
