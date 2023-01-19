package com.example.olapark.nav.parks;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.olapark.R;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Calendar;

public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private Button day;
    private Park park;
    private View v;

    public SelectDateFragment(Button day, Park park, View v) {
        this.day = day;
        this.park = park;
        this.v = v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, yy, mm, dd);
    }

    public void onDateSet(DatePicker view, int yy, int mm, int dd) {

        mm += 1;

        String month = String.valueOf(mm);
        String d = String.valueOf(dd);

        if(mm < 10){
            month = "0" + mm;
        }

        if(dd < 10){
            d = "0" + d;
        }

        day.setText(d + "/" + month + "/" + yy);

        calculatePrice();
    }

    private void calculatePrice(){

        Button entry_date_day = v.findViewById(R.id.entry_date_day_value);
        Button entry_date_hour = v.findViewById(R.id.entry_date_hour_value);
        Button departure_date_day = v.findViewById(R.id.departure_date_day_value);
        Button departure_date_hour = v.findViewById(R.id.departure_date_hour_value);

        String entry_date_day_value = entry_date_day.getText().toString();
        String entry_date_hour_value = entry_date_hour.getText().toString();
        String departure_date_day_value = departure_date_day.getText().toString();
        String departure_date_hour_value = departure_date_hour.getText().toString();

        System.out.println(entry_date_hour_value);

        if(!"Select date".equals(entry_date_day_value) && !"Select hour".equals(entry_date_hour_value) &&
                !"Select date".equals(departure_date_day_value) && !"Select hour".equals(departure_date_hour_value)){

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
                hours += 1;

                if(hours > 0){
                    float totalPrice = (float) (hours * price);

                    TextView reservation_price_value = v.findViewById(R.id.reservation_price_value);
                    reservation_price_value.setText(totalPrice + "€");
                }

            }
        }
    }


}
