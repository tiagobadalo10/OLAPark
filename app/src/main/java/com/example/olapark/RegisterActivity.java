package com.example.olapark;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = FirebaseFirestore.getInstance();

        userRegister();
    }

    public void userRegister(){

        Button register = findViewById(R.id.new_register);
        register.setOnClickListener(v -> {

            String email = "", password = "", username = "", phoneNumber = "", confirmPassword = "";

            EditText emailForm = findViewById(R.id.new_email);
            EditText usernameForm = findViewById(R.id.new_username);
            EditText phoneNumberForm = findViewById(R.id.new_phone);
            EditText passwordForm = findViewById(R.id.new_password);
            EditText confirmPasswordForm = findViewById(R.id.confirm_password);

            email = String.valueOf(emailForm.getText());
            username = String.valueOf(usernameForm.getText());
            phoneNumber = String.valueOf(phoneNumberForm.getText());
            password = String.valueOf(passwordForm.getText());
            confirmPassword = String.valueOf(confirmPasswordForm.getText());

            if(email.length() != 0 && password.length() != 0 && phoneNumber.length() == 9 && confirmPassword.length() != 0 && password.equals(confirmPassword)){

                String finalEmail = email;
                String finalUsername = username;
                String finalPhoneNumber = phoneNumber;

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, task -> {

                            if(!task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Registration failed. " + task.getException(), Toast.LENGTH_LONG);
                            }
                            else{
                                saveUserData(finalEmail, finalUsername, Integer.parseInt(finalPhoneNumber));

                                Intent intent;
                                intent = new Intent(RegisterActivity.this, MainMenuActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
            }
        });
    }

    public void saveUserData(String email, String username, int phoneNumber){
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("phone-number", phoneNumber);

        db.collection("users").document(email)
                .set(user);
    }
}