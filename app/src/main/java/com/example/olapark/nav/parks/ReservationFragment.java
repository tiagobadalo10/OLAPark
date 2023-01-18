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

        Calendar calendar = Calendar.getInstance();

        EditText entry_date_day_value = v.findViewById(R.id.entry_date_day_value);
        entry_date_day_value.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                new DatePickerDialog(getContext(),
                        (view1, year, month, dayOfMonth) -> {
                            entry_date_day_value.setText(dayOfMonth + "/" + month + "/" + year);
                            calculatePrice();
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
            }
        });

        EditText entry_date_hour_value = v.findViewById(R.id.entry_date_hour_value);
        entry_date_hour_value.setOnClickListener(view -> {
            new TimePickerDialog(getContext(),
                    (view1, hourOfDay, minute) -> {
                        entry_date_hour_value.setText(hourOfDay + ":" + minute);
                        calculatePrice();
                    },
                    calendar.get(Calendar.HOUR),
                    calendar.get(Calendar.MINUTE),
                    true);
        });

        EditText departure_date_day_value = v.findViewById(R.id.departure_date_day_value);
        departure_date_day_value.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                new DatePickerDialog(getContext(),
                        (view1, year, month, dayOfMonth) -> {
                            departure_date_day_value.setText(dayOfMonth + "/" + month + "/" + year);
                            calculatePrice();
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
            }
        });

        EditText departure_date_hour_value = v.findViewById(R.id.departure_date_hour_value);
        departure_date_hour_value.setOnClickListener(view -> {
            new TimePickerDialog(getContext(),
                    (view1, hourOfDay, minute) -> {
                        departure_date_hour_value.setText(hourOfDay + ":" + minute);
                        calculatePrice();
                    },
                    calendar.get(Calendar.HOUR),
                    calendar.get(Calendar.MINUTE),
                    true);
        });

        TextView reservation_price_value = v.findViewById(R.id.reservation_price_value);

        Button button = v.findViewById(R.id.reservation_submit);
        button.setOnClickListener(view -> {

            float balance = sp.getFloat("balance", 0);

            float total_price = Float.parseFloat(reservation_price_value.getText().toString());

            if(calculatePrice() && balance >= total_price){

                addToPayments(park.getName(), String.valueOf(total_price));
                updateBalance(balance - total_price);
                increaseCoins();
            }

        });
    }

    private void increaseCoins() {

        String username = sp.getString("username", "");

        int coins = sp.getInt("coins", 0) + 1;

        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("coins", coins);

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

                        // Get all payments
                        Map<Integer, ArrayList<String>> payments = (Map<Integer, ArrayList<String>>) document.get("payments");

                        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){

                            int number;

                            if (payments.isEmpty()) {

                                number = 1;
                            }

                            else {

                                number = payments.size();
                            }

                            String now = LocalDateTime.now().toString();

                            String fst = now.split("T")[0];
                            String snd = now.split("T")[1].split(":")[0] + ":" +  now.split("T")[1].split(":")[1];

                            String date = fst + " " + snd;

                            ArrayList<String> payment = new ArrayList<>();
                            payment.add(date);
                            payment.add(name);
                            payment.add(value);

                            payments.put(number, payment);

                            db.collection("users").document(username).update("payments", payments);

                        }

                    }
                }
        );

    }


    private boolean calculatePrice(){

        EditText entry_date_day = v.findViewById(R.id.entry_date_day_value);
        EditText entry_date_hour = v.findViewById(R.id.entry_date_hour_value);
        EditText departure_date_day = v.findViewById(R.id.departure_date_day_value);
        EditText departure_date_hour = v.findViewById(R.id.departure_date_hour_value);

        String entry_date_day_value = entry_date_day.getText().toString();
        String entry_date_hour_value = entry_date_hour.getText().toString();
        String departure_date_day_value = departure_date_day.getText().toString();
        String departure_date_hour_value = departure_date_hour.getText().toString();

        if(!"".equals(entry_date_day_value) && !"".equals(entry_date_hour_value) &&
            !"".equals(departure_date_day_value) && !"".equals(departure_date_hour_value)){

            double price = park.getPricePerHour();

            String[] aux = entry_date_day_value.split("/");
            int day_entry = Integer.parseInt(aux[0]);
            int month_entry = Integer.parseInt(aux[1]);
            int year_entry = Integer.parseInt(aux[2]);

            aux = entry_date_hour_value.split(":");
            int hour_entry = Integer.parseInt(aux[0]);
            int minute_entry = Integer.parseInt(aux[1]);

            aux = departure_date_day_value.split("/");
            int day_departure = Integer.parseInt(aux[0]);
            int month_departure = Integer.parseInt(aux[1]);
            int year_departure = Integer.parseInt(aux[2]);

            aux = departure_date_hour_value.split(":");
            int hour_departure = Integer.parseInt(aux[0]);
            int minute_departure = Integer.parseInt(aux[1]);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDateTime start = LocalDateTime.of(year_entry,
                        month_entry,
                        day_entry,
                        hour_entry,
                        minute_entry);

                LocalDateTime end = LocalDateTime.of(year_departure,
                        month_departure,
                        day_departure,
                        hour_departure,
                        minute_departure);

                Duration duration = Duration.between(start, end);
                long hours = duration.toHours();

                float totalPrice = (float) (hours * price);

                TextView reservation_price_value = v.findViewById(R.id.reservation_price_value);
                reservation_price_value.setText(String.valueOf(totalPrice));

                return true;
            }

            return false;

        }

        return false;
    }

    public void setPark(Park park) {

        this.park = park;

    }
}