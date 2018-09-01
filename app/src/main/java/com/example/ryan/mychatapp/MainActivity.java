package com.example.ryan.mychatapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar  mToolbar;

    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsPagerAdapter myTabsPagerAdapter;

     FirebaseUser currentuser;
     private DatabaseReference UsersReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentuser = mAuth.getCurrentUser();
        if(currentuser != null)
        {
            String online_user_id = mAuth.getCurrentUser().getUid();
            UsersReference = FirebaseDatabase.getInstance()
                    .getReference().child("Users").child(online_user_id);
        }


        //tab for mainActivity
        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsPagerAdapter);
        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);



        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("MyChat");
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentuser = mAuth.getCurrentUser();
        if(currentuser == null) //invalid user
        {
            LogOutUser();
        }
        else if(currentuser != null)
        {
          UsersReference.child("online").setValue("true");
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        if(currentuser != null)
        {
            UsersReference.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    private void LogOutUser() {
        Intent startpageIntent = new Intent(MainActivity.this, StartPageActivity.class);
        startpageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity(startpageIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       super.onCreateOptionsMenu(menu);
       getMenuInflater().inflate(R.menu.main_menu,menu);
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
         if(item.getItemId() == R.id.main_logout_button)//logoout user
         {
             if(currentuser != null)
             {
                 UsersReference.child("online").setValue(ServerValue.TIMESTAMP);
             }
             mAuth.signOut();
             LogOutUser(); //go back mainscreen to ask the user to sign in again
         }
               if(item.getItemId() ==R.id.main_account_settings_button)
        {
            Intent SettingActivityIntent = new Intent(MainActivity.this,SettingActivity.class);
            startActivity(SettingActivityIntent);
        }
        if(item.getItemId() ==R.id.search_users_button)
        {
            Intent allUsersIntent = new Intent(MainActivity.this,SearchUsersActivity.class);
            startActivity(allUsersIntent );
        }
        if(item.getItemId() ==R.id.all_users_button)
        {
            Intent allUsersIntent = new Intent(MainActivity.this, AllUsersActivity.class);
            startActivity(allUsersIntent );
        }

       return true;
    }
}
