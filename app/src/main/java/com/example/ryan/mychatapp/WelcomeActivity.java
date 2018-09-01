package com.example.ryan.mychatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Thread thead = new Thread()
        {
            public void run()
            {
               try {
                   sleep(3000);
               }
               catch (Exception e)
               {
                   e.printStackTrace();
               }
               finally {
                   Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                   startActivity(intent);
               }
            }
        };
        thead.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
