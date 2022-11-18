package com.example.olapark;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(() -> {
            Intent intent;
            // Verify if there is an logged account

            // If there isn't, change to login activiy
            intent = new Intent(MainActivity.this, LoginActivity.class);
            // Else, change to main menu activity

            startActivity(intent);
            finish();
        }, 1000);

    }

}