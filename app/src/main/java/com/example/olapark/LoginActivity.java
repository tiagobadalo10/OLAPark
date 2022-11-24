package com.example.olapark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

            String email = "", password = "";

            EditText emailForm = findViewById(R.id.email);
            EditText passwordForm = findViewById(R.id.password);

            email = String.valueOf(emailForm.getText());
            password = String.valueOf(passwordForm.getText());

            if(email.length() != 0 && password.length() != 0) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Authentication failed. " + task.getException(), Toast.LENGTH_LONG);
                    } else {
                        Intent intent;
                        intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
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