package com.example.olapark;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userRegister();
    }

    public void userRegister(){

        Button register = findViewById(R.id.new_register);
        register.setOnClickListener(v -> {

            String email = "", password = "";

            EditText emailForm = findViewById(R.id.new_email);
            EditText passwordForm = findViewById(R.id.new_password);

            email = String.valueOf(emailForm.getText());
            password = String.valueOf(passwordForm.getText());


            if(email.length() != 0 && password.length() != 0){
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, task -> {

                            if(!task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Registration failed. " + task.getException(), Toast.LENGTH_LONG);
                            }
                            else{
                                Intent intent;
                                intent = new Intent(RegisterActivity.this, MainMenuActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
            }
        });


    }
}