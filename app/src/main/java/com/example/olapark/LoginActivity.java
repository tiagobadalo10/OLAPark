package com.example.olapark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // When login button is clicked
        Button login = findViewById(R.id.login);
        login.setOnClickListener(v -> {
            Intent intent;
            intent = new Intent(LoginActivity.this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        });

        // When register button is clicked
        Button register = findViewById(R.id.register);
        register.setOnClickListener(v -> {
            Intent intent;
            intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }
}