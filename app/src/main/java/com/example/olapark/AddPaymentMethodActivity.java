package com.example.olapark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddPaymentMethodActivity extends AppCompatActivity {

    private SharedPreferences sp;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_method);

        db = FirebaseFirestore.getInstance();
        sp = getSharedPreferences("auto-login", MODE_PRIVATE);

        saveCard();

    }

    private void saveCard() {

        Button save_card = findViewById(R.id.save_card);

        save_card.setOnClickListener(v -> {

            EditText card_number = findViewById(R.id.card_number);
            EditText card_name = findViewById(R.id.card_name);
            EditText card_date = findViewById(R.id.card_date);
            EditText card_cvv = findViewById(R.id.card_CVV);

            String number = card_number.getText().toString();
            String name = card_name.getText().toString();
            String date = card_date.getText().toString();
            String cvv = card_cvv.getText().toString();

            if(number != "" && name != "" && date != "" && cvv != ""){

                hideNumbers(number);

                String username = sp.getString("username", "");

                Map<String, Object> paymentMethod = new HashMap<>();

                paymentMethod.put("number", number);
                paymentMethod.put("name", name);
                paymentMethod.put("date", date);
                paymentMethod.put("cvv", cvv);

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("card", number);
                editor.commit();

                db.collection("users").document(username).update("payment-method", paymentMethod);

            }

            finish();
        });

    }

    private String hideNumbers(String number) {

        char [] numbers = number.toCharArray();
        for(int i = 0; i < numbers.length - 4; i++){
            numbers[i] = '*';
        }

        return String.valueOf(numbers);


    }
}