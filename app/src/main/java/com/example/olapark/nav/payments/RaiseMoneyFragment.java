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

        Button raise_button = v.findViewById(R.id.raise_button);

        raise_button.setOnClickListener(view -> {

            EditText raise_amount = v.findViewById(R.id.raise_amount);

            String amount = raise_amount.getText().toString();

            if(amount.length() != 0){

                int value = Integer.parseInt(amount);

                // Check if the value is less or equal than balance

                TextView raise_error = v.findViewById(R.id.raise_error);

                float balance = (float) sp.getFloat("balance", 0);

                if(balance < value){

                    raise_error.setVisibility(View.VISIBLE);
                }

                else {

                    String username = sp.getString("username" , "");

                    updateBalanceSP(value, balance);

                    updateBalanceDB(username, value, balance);

                    raise_error.setVisibility(View.INVISIBLE);

                    ConstraintLayout cl = v.findViewById(R.id.fragment_raise);
                    cl.removeAllViewsInLayout();

                    getFragmentManager().beginTransaction().replace(R.id.fragment_raise, new PaymentsFragment(),"Payments").commit();
                }

            }

        });

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