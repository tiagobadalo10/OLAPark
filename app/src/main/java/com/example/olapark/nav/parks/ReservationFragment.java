package com.example.olapark.nav.parks;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.olapark.R;

import java.util.Calendar;

public class ReservationFragment extends DialogFragment {

    private Park park;

    private View v;

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
                    },
                    calendar.get(Calendar.HOUR),
                    calendar.get(Calendar.MINUTE),
                    true);
        });
    }

    public void setPark(Park park) {

        this.park = park;

    }
}