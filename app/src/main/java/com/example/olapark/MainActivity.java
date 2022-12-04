package com.example.olapark;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(() -> {
            Intent intent;
            sp = getSharedPreferences("auto-login", MODE_PRIVATE);
            // Verify if there is an logged account
            if(sp.contains("username")){
                intent = new Intent(MainActivity.this, MainMenuActivity.class);
            }
            // If there isn't, change to login activity
            else{
                intent = new Intent(MainActivity.this, LoginActivity.class);

            }

            startActivity(intent);
            finish();

        }, 1000);

    }

}