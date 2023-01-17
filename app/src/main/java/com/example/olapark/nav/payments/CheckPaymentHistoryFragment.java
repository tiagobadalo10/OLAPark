package com.example.olapark.nav.payments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.olapark.LoginActivity;
import com.example.olapark.MainMenuActivity;
import com.example.olapark.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firestore.v1.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;
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

    // data, park, valor
    private void addPayments(String username) {

        db.collection("users")
                .document(username)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){

                        DocumentSnapshot document = task.getResult();

                        Map<Integer, ArrayList<String>> payments = (Map<Integer, ArrayList<String>>) document.get("payments");

                        for(Map.Entry<Integer, ArrayList<String>> payment: payments.entrySet()){
                            
                        }
                    }
                });





    }


}