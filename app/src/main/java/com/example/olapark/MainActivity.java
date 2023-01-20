package com.example.olapark;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;


import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sp;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        new Handler().postDelayed(() -> {
            Intent i;
            sp = getSharedPreferences("auto-login", MODE_PRIVATE);
            // Verify if there is an logged account
            if (sp.contains("username")) {
                i = new Intent(MainActivity.this, MainMenuActivity.class);

            }
            // If there isn't, change to login activity
            else {
                i = new Intent(MainActivity.this, LoginActivity.class);
            }

            startActivity(i);
            finish();

        }, 5000);

    }

}