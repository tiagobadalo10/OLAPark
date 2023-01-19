package com.example.olapark.nav.parks;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import com.example.olapark.ActivityRecognitionService;
import com.example.olapark.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Locale;

public class SelectHourFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private Button hour;
    private Park park;
    private View v;

    private SharedPreferences sp;

    private FirebaseFirestore db;

    public SelectHourFragment(Button hour, Park park, View v, SharedPreferences sp) {
        this.hour = hour;
        this.park = park;
        this.v = v;

        this.sp = sp;
        this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance(Locale.UK);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hour, min, true);
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        String h = String.valueOf(hourOfDay);
        String m = String.valueOf(minute);

        if (hourOfDay < 10) {
            h = "0" + hourOfDay;
        }

        if (minute < 10) {
            m = "0" + minute;
        }

        hour.setText(h + ":" + m);
        calculatePrice();
    }

    private void calculatePrice() {

        Button entry_date_day = v.findViewById(R.id.entry_date_day_value);
        Button entry_date_hour = v.findViewById(R.id.entry_date_hour_value);
        Button departure_date_day = v.findViewById(R.id.departure_date_day_value);
        Button departure_date_hour = v.findViewById(R.id.departure_date_hour_value);

        String entry_date_day_value = entry_date_day.getText().toString();
        String entry_date_hour_value = entry_date_hour.getText().toString();
        String departure_date_day_value = departure_date_day.getText().toString();
        String departure_date_hour_value = departure_date_hour.getText().toString();

        if (!"Select date".equals(entry_date_day_value) && !"Select hour".equals(entry_date_hour_value) &&
                !"Select date".equals(departure_date_day_value) && !"Select hour".equals(departure_date_hour_value)) {

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

                if (hours == 0 || (minute_departure > minute_entry && hour_entry == hour_departure)) {
                    hours += 1;
                }

                if (hours > 0) {
                    float totalPrice = (float) (hours * price);

                    String username = sp.getString("username", "");

                    db.collection("users").document(username).get().addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            DocumentSnapshot document = task.getResult();

                            long reward = (long) document.get("reward");

                            TextView reservation_price_value = v.findViewById(R.id.reservation_price_value);

                            reservation_price_value.setText(totalPrice - totalPrice * reward / 100 + "€");

                        }

                    });
                }
            }
        }
    }
}