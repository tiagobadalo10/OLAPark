package com.example.olapark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

public class EditProfileActivity extends AppCompatActivity {

    private String username;
    private String email;
    private Long phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

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
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
        finish();
    }

    private void saveEdition(){

        String current_username, current_email;
        Long current_phone_number;

        EditText username_field = findViewById(R.id.edit_username);
        EditText phone_number_field = findViewById(R.id.edit_phone_number);
        EditText email_field = findViewById(R.id.edit_email_address);

        current_username = String.valueOf(username_field.getText());
        current_phone_number = Long.getLong(String.valueOf(phone_number_field.getText()));
        current_email = String.valueOf(email_field.getText());

        if(current_username != username){
            changeUsername(current_username);
        }

        if(current_phone_number != phone_number){
            changePhoneNumber(current_phone_number);
        }

        if(current_email != email){
            changeEmail();
        }


    }

    // Database
    private void changeUsername(String current_username) {
    }

    // Database
    private void changePhoneNumber(Long current_phone_number) {
    }

    // Auth and Database
    private void changeEmail() {
    }


}