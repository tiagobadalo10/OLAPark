package com.example.olapark.nav.payments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.olapark.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class RaiseMoneyFragment extends Fragment {

    private View v;

    private SharedPreferences sp;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_raise_money, container, false);

        sp = getActivity().getSharedPreferences("auto-login", MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();

        withdrawMoney();

        return v;
    }

    private void withdrawMoney() {



    }

    private void updateBalanceDB(String username, int amount, float balance) {

        db.collection("users").document(username).update("balance", balance - amount);

    }
    private void updateBalanceSP(int amount, float balance) {

        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat("balance", balance - amount);
        editor.commit();

    }
}