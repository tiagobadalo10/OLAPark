package com.example.olapark.nav.parks;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.olapark.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ReservationFragment extends DialogFragment {

    private Park park;
    private View v;

    private FirebaseFirestore db;

    private SharedPreferences sp;

    public static ReservationFragment newInstance(String title) {
        ReservationFragment yourDialogFragment = new ReservationFragment();

        Bundle args = new Bundle();
        args.putString("title", title);
        yourDialogFragment.setArguments(args);

        return yourDialogFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_reservation, container, false);
        db = FirebaseFirestore.getInstance();
        sp = getActivity().getSharedPreferences("auto-login", Context.MODE_PRIVATE);

        submitReservation();

        return v;
    }

    private void submitReservation() {

        Button entry_date_day_value = v.findViewById(R.id.entry_date_day_value);
        entry_date_day_value.setOnClickListener(view -> {

            DialogFragment dateFragment = new SelectDateFragment(entry_date_day_value, park, v);
            dateFragment.show(getFragmentManager(), "Early Date Picker");

        });

        Button entry_date_hour_value = v.findViewById(R.id.entry_date_hour_value);
        entry_date_hour_value.setOnClickListener(view -> {

            DialogFragment dateFragment = new SelectHourFragment(entry_date_hour_value, park, v);
            dateFragment.show(getFragmentManager(), "Early Hour Picker");

        });

        Button departure_date_day_value = v.findViewById(R.id.departure_date_day_value);
        departure_date_day_value.setOnClickListener(view -> {

            DialogFragment dateFragment = new SelectDateFragment(departure_date_day_value, park, v);
            dateFragment.show(getFragmentManager(), "Departure Date Picker");

        });

        Button departure_date_hour_value = v.findViewById(R.id.departure_date_hour_value);
        departure_date_hour_value.setOnClickListener(view -> {

            DialogFragment dateFragment = new SelectHourFragment(departure_date_hour_value, park, v);
            dateFragment.show(getFragmentManager(), "Departure Hour Picker");

        });

        TextView reservation_price_value = v.findViewById(R.id.reservation_price_value);

        Button button = v.findViewById(R.id.reservation_submit);
        button.setOnClickListener(view -> {

            float balance = sp.getFloat("balance", 0);

            String price = (String) reservation_price_value.getText();

            if(price != ""){

                float total_price = Float.parseFloat(price.split("€")[0]);

                if(balance >= total_price){
                    addToPayments(park.getName(), String.valueOf(total_price));
                    updateBalance(balance - total_price);
                    increaseCoins();

                    dismiss();
                }
            }
        });
    }

    private void increaseCoins() {

        String username = sp.getString("username", "");

        int coins = sp.getInt("coins", 0) + 1;

        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("coins", coins);
        editor.apply();

        db.collection("users").document(username).update("coins", coins);

    }

    private void updateBalance(float new_balance) {

        SharedPreferences.Editor editor = sp.edit();

        String username = sp.getString("username", "");

        editor.putFloat("balance", new_balance);

        editor.apply();

        db.collection("users").document(username).update("balance", new_balance);

    }

    private void addToPayments(String name, String value) {

        String username = sp.getString("username", "");

        db.collection("users").document(username).get().addOnCompleteListener(
                task -> {
                    if(task.isSuccessful()){

                        DocumentSnapshot document = task.getResult();

                        Map<String, String> payments = (Map<String, String>) document.get("payments");

                        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){

                            int number;

                            if (payments.isEmpty()) {

                                number = 1;
                            }

                            else {

                                number = payments.size() + 1;
                            }

                            String now = LocalDateTime.now().toString();

                            String fst = now.split("T")[0];
                            String snd = now.split("T")[1].split(":")[0] + ":" +  now.split("T")[1].split(":")[1];

                            String date = fst + " " + snd;

                            payments.put(String.valueOf(number), date + "%" + name + "%" + value);

                            db.collection("users").document(username).update("payments", payments);

                        }

                    }
                }
        );

    }
    public void setPark(Park park) {

        this.park = park;

    }
}