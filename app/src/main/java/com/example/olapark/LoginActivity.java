package com.example.olapark;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db;
    private SharedPreferences sp;
    private static String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = FirebaseFirestore.getInstance();
        sp = getSharedPreferences("auto-login", MODE_PRIVATE);

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

                String finalEmail = email;

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Authentication failed. " + task.getException(), Toast.LENGTH_LONG);
                    } else {
                        afterAuthentication(finalEmail);
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

    public void afterAuthentication(String email){

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document: task.getResult()){

                            this.username = document.getId();

                            saveUserDataSP(username);

                            Intent intent;
                            intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                            intent.putExtra("username", username);

                            startActivity(intent);
                            finish();

                            break;
                        }
                    }
                });

    }

    public void saveUserDataSP(String username){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", username);

        editor.commit();
    }
}