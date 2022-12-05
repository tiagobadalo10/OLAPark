package com.example.olapark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private String username;
    private String email;
    private Long phone_number;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        db = FirebaseFirestore.getInstance();

        Bundle user_info = getIntent().getExtras();
        username = user_info.getString("username");
        phone_number = user_info.getLong("phone_number");
        email = user_info.getString("email");

        loadCurrentInfo(username, phone_number, email);

        cancelEdition();

        saveEdition();
    }

    private void loadCurrentInfo(String username, Long phone_number, String email) {
        EditText username_field = findViewById(R.id.edit_username);
        EditText phone_number_field = findViewById(R.id.edit_phone_number);
        EditText email_field = findViewById(R.id.edit_email_address);

        username_field.setText(username);
        phone_number_field.setText(String.valueOf(phone_number));
        email_field.setText(email);
    }

    private void cancelEdition(){

        Button cancel = findViewById(R.id.edit_cancel);
        cancel.setOnClickListener(v -> {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void saveEdition(){

        Button save = findViewById(R.id.edit_save);
        save.setOnClickListener(v -> {

            String current_username, current_email;
            Long current_phone_number;

            EditText username_field = findViewById(R.id.edit_username);
            EditText phone_number_field = findViewById(R.id.edit_phone_number);
            EditText email_field = findViewById(R.id.edit_email_address);

            current_username = String.valueOf(username_field.getText());
            current_phone_number = Long.getLong(String.valueOf(phone_number_field.getText()));
            current_email = String.valueOf(email_field.getText());

            if(current_username != username){
                changeUsername(username, current_username);
            }

            if(current_phone_number != phone_number){
                changePhoneNumber(username, current_phone_number);
            }

            if(current_email != email){
                changeEmail();
            }

            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
            finish();
        });
    }

    // Database
    private void changeUsername(String username, String current_username) {
        db.collection("users").document(username).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                Map<String, Object> data = document.getData();

                db.collection("users").document(current_username)
                        .set(data).addOnCompleteListener(task1 -> {
                            if(task.isSuccessful()){
                                db.collection("users").document(username).delete();
                            }
                        });
            }
        });


    }

    // Database
    private void changePhoneNumber(String username, Long current_phone_number) {

    }

    // Auth and Database
    private void changeEmail() {
        // get current user in FirebaseAuth
    }


}