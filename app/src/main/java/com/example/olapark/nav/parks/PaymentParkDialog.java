package com.example.olapark.nav.parks;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.olapark.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

public class PaymentParkDialog extends DialogFragment {

    private View view;
    private Park park;
    private GoogleMap map;
    private LatLng currentPosition;

    private final String url = "https://roads.googleapis.com/v1/snapToRoads?interpolate=true&path=";
    private final String key = "AIzaSyBx64LbDqZGT7otVA_QFu_QHJAHeA7A8kQ";
    private MapsFragment mapFragment;

    private EditText editTextHours;
    private TextView textViewPrice;

    public static PaymentParkDialog newInstance(String title) {
        PaymentParkDialog yourDialogFragment = new PaymentParkDialog();

        Bundle args = new Bundle();
        args.putString("title", title);
        yourDialogFragment.setArguments(args);

        return yourDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_payment_park_dialog, container, false);

        confirm();
        cancel();

        editTextHours = view.findViewById(R.id.editText_hours);
        textViewPrice = view.findViewById(R.id.textView_price_value);

        editTextHours.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for this example
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String hoursText = s.toString();
                if (!hoursText.isEmpty()) {
                    int hours = Integer.parseInt(hoursText);
                    if (hours < 0 || hours > 1000) {
                        // Show an error message
                        Toast.makeText(getContext(), "Valor inválido", Toast.LENGTH_SHORT).show();
                        editTextHours.setText("");
                        textViewPrice.setText("0,00 Є");
                    } else {
                        double price = hours * park.getPricePerHour();
                        textViewPrice.setText(String.format(Locale.getDefault(), "%.2f Є", price));
                    }
                } else {
                    textViewPrice.setText("0,00 Є");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed for this example
            }
        });

        return view;
    }

    private void confirm() {

        Button confirm = view.findViewById(R.id.button_confirm);

        confirm.setOnClickListener(v -> {

            //TextView textViewPriceValue = view.findViewById(R.id.textView_price_value);
            //String priceValue = textViewPriceValue.getText().toString();
            //double price = Double.parseDouble(priceValue.split(" ")[0]);

            //TODO descontar na wallet

            dismiss();
        });

    }

    private void cancel() {

        Button cancel = view.findViewById(R.id.button_cancel);

        cancel.setOnClickListener(v -> {
            dismiss();
        });

    }

    public void setPark(Park park) {
        if (park == null) {
            dismiss();
        }
        this.park = park;
    }

    public void setMap(GoogleMap mMap) {
        this.map = mMap;
    }

    public void setMapFragment(MapsFragment fragment) {this.mapFragment = fragment;}

    public void setCurrentPosition(LatLng currentPosition) {

        this.currentPosition = currentPosition;

    }
}
