package com.example.olapark.nav.payments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.olapark.LoginActivity;
import com.example.olapark.MainMenuActivity;
import com.example.olapark.ProfileActivity;
import com.example.olapark.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firestore.v1.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CheckPaymentHistoryFragment extends Fragment {

    private View v;

    private SharedPreferences sp;

    private FirebaseFirestore db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_check_payment_history, container, false);

        sp = getActivity().getSharedPreferences("auto-login", MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();

        String username = sp.getString("username" , "");

        addPayments(username);

        return v;
    }

    private void addPayments(String username) {

        db.collection("users")
                .document(username)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){

                        DocumentSnapshot document = task.getResult();

                        Map<String, String> payments = (Map<String, String>) document.get("payments");

                        TableLayout table = v.findViewById(R.id.table_payments);

                        for(Map.Entry<String, String> payment: payments.entrySet()){

                            String number = payment.getKey();
                            String values = payment.getValue();

                            String[] aux = values.split("%");

                            TableRow row = new TableRow(getContext());
                            TableRow.LayoutParams paramsExample = new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT,0.4f);

                            TextView numberView = new TextView(getContext());
                            numberView.setText(number);
                            numberView.setTextSize(16);
                            numberView.setLayoutParams(paramsExample);

                            TextView dateView = new TextView(getContext());
                            numberView.setText(aux[0]);
                            numberView.setTextSize(16);
                            numberView.setLayoutParams(paramsExample);

                            TextView parkView = new TextView(getContext());
                            numberView.setText(aux[1]);
                            numberView.setTextSize(16);
                            numberView.setLayoutParams(paramsExample);

                            TextView valueView = new TextView(getContext());
                            numberView.setText(aux[2]);
                            numberView.setTextSize(16);
                            numberView.setLayoutParams(paramsExample);

                            row.addView(numberView);
                            row.addView(dateView);
                            row.addView(parkView);
                            row.addView(valueView);

                            table.addView(row);

                        }
                    }
                });





    }


}