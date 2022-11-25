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
import com.google.firebase.auth.FirebaseAuth;

public class ChangePasswordActivity extends AppCompatActivity {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Button reset = findViewById(R.id.reset);
        reset.setOnClickListener(v -> {
            EditText email = findViewById(R.id.email2);
            resetPassword(String.valueOf(email.getText()));
        });
    }

    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    public void resetPassword(String email){
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(ChangePasswordActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else{
                        Toast.makeText(ChangePasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}