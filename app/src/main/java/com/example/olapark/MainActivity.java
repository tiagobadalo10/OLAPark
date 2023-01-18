package com.example.olapark;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;


import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sp;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        //Bundle extras = getIntent().getExtras();

        new Handler().postDelayed(() -> {
            Intent i;
            sp = getSharedPreferences("auto-login", MODE_PRIVATE);
            // Verify if there is an logged account
            if (sp.contains("username")) {
                i = new Intent(MainActivity.this, MainMenuActivity.class);
                //if (extras != null) {
                //    boolean openOccupationDialog = extras.getBoolean("openOccupationDialog");
                //    i.putExtra("openOccupationDialog", true);
                //    Toast.makeText(getApplicationContext(), "MainActivity", Toast.LENGTH_SHORT).show();
                //    getIntent().removeExtra("openOccupationDialog");
                //}
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