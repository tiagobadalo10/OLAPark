package com.example.olapark;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = FirebaseFirestore.getInstance();
        sp = getSharedPreferences("auto-login", MODE_PRIVATE);

        userRegister();
    }

    public void userRegister(){

        Button register = findViewById(R.id.new_register);
        register.setOnClickListener(v -> {

            String email, password, username, phoneNumber, confirmPassword;

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

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, task -> {

                            if(task.isSuccessful()){
                                saveUserDataDB(email, username, Long.parseLong(phoneNumber));
                                saveUserDataSP(username, password);

                                Intent intent;
                                intent = new Intent(RegisterActivity.this, MainMenuActivity.class);
                                intent.putExtra("username", username);

                                startActivity(intent);
                                finish();
                            }
                        });
            }
        });
    }

    public void saveUserDataDB(String email, String username, Long phoneNumber){
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("phone-number", phoneNumber);
        user.put("balance", 0.0);
        user.put("coins", 0);

        Map<String, Boolean> settings = new HashMap<>();
        settings.put("auto-payment", false);

        user.put("settings", settings);

        // number, [data, local, value]
        Map<Integer, ArrayList<String>> payments = new HashMap<>();
        user.put("payments", payments);

        db.collection("users").document(username)
                .set(user);
    }

    public void saveUserDataSP(String username, String password){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putFloat("balance", 0);
        editor.putInt("coins", 0);

        editor.commit();
    }


    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}