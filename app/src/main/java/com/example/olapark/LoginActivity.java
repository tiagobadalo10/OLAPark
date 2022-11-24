package com.example.olapark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // When login button is clicked
        userLogin();

        // When register button is clicked
        userRegister();

    }

    public void userLogin(){
        Button login = findViewById(R.id.login);
        login.setOnClickListener(v -> {

            Intent intent;
            intent = new Intent(LoginActivity.this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void userRegister(){
        Button register = findViewById(R.id.register);
        register.setOnClickListener(v -> {

            Intent intent;
            intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }
}