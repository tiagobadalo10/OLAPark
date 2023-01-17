package com.example.olapark;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;


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

        setSettings();

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

    private void setSettings() {

        HashMap<String, Boolean> settings = new HashMap<>();

        db.collection("settings").document("settings").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                DocumentSnapshot document = task.getResult();
                // existing settings
                if (document.exists()) {
                    settings.put("auto-payment", document.getBoolean("auto-payment"));
                }
                // default settings
                else {
                    settings.put("auto-payment", false);
                }

                saveSettingsSP(settings);
            }
        });


    }

    private void saveSettingsSP(HashMap<String, Boolean> settings) {

        sp = getSharedPreferences("settings", MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            settings.forEach(editor::putBoolean);
        }

        editor.commit();
    }

}