package com.example.olapark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.olapark.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Profile extends AppCompatActivity {

    private String username;
    private FirebaseFirestore db;

    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView phoneNumberTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences sh = getSharedPreferences("auto-login", MODE_PRIVATE);

        username = sh.getString("username", null);
        db = FirebaseFirestore.getInstance();

        usernameTextView = (TextView) findViewById(R.id.username_txt);
        emailTextView = (TextView) findViewById(R.id.email_txt);
        phoneNumberTextView = (TextView) findViewById(R.id.phone_number_txt);

        setProfileInfo();
    }

    private void setProfileInfo() {

        db.collection("users").document(username).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String email = documentSnapshot.getString("email");
                            Long phoneNumber = documentSnapshot.getLong("phone-number");

                            //set values
                            usernameTextView.setText(username);
                            emailTextView.setText(email);
                            phoneNumberTextView.setText(String.valueOf(phoneNumber));
                        }
                    }
                });

    }

}