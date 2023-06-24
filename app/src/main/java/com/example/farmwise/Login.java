package com.example.firstapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class Login extends AppCompatActivity{
    TabLayout tabLayout;
    ViewPager2 viewPager;
    FloatingActionButton google;

    FirebaseUser user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//
//        TextInputEditText email = findViewById(R.id.loginEmail);
//        TextInputEditText textPassword = findViewById(R.id.password);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(user != null){
            mainActivity();
        }
    }

    private void mainActivity(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}