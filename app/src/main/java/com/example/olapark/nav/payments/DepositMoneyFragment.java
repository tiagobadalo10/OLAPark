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

public class DepositMoneyFragment extends Fragment {

    private View v;
    private SharedPreferences sp;

    private FirebaseFirestore db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            v = inflater.inflate(R.layout.fragment_deposit_money, container, false);

            sp = getActivity().getSharedPreferences("auto-login", MODE_PRIVATE);
            db = FirebaseFirestore.getInstance();

            depositMoney();

            return v;
    }

    private void depositMoney() {

        Button deposit_button = v.findViewById(R.id.deposit_button);

        deposit_button.setOnClickListener(view -> {

            EditText deposit_amount = v.findViewById(R.id.deposit_amount);

            String amount = deposit_amount.getText().toString();

            if(amount.length() != 0){

                int value = Integer.parseInt(amount);

                // Check if the value is less than 5

                TextView deposit_error = v.findViewById(R.id.deposit_error);

                if(value < 5){

                    deposit_error.setVisibility(View.VISIBLE);

                }

                else {

                    String username = sp.getString("username" , "");

                    float balance = (float) sp.getFloat("balance", 0);

                    updateBalanceSP(value, balance);

                    updateBalanceDB(username, value, balance);

                    deposit_error.setVisibility(View.INVISIBLE);

                    ConstraintLayout cl = v.findViewById(R.id.fragment_deposit);
                    cl.removeAllViewsInLayout();

                    getFragmentManager().beginTransaction().replace(R.id.fragment_deposit, new PaymentsFragment(),"Payments").commit();

                }
            }
        });

    }

    private void updateBalanceDB(String username, int amount, float balance) {

        db.collection("users").document(username).update("balance", balance + amount);

    }
    private void updateBalanceSP(int amount, float balance) {

        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat("balance", balance + amount);
        editor.commit();

    }
}