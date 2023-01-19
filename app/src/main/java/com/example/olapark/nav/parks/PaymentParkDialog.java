package com.example.olapark.nav.parks;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

public class PaymentParkDialog extends DialogFragment {

    private View view;
    private Park park;
    private SharedPreferences sp;
    private FirebaseFirestore db;
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

        sp = getActivity().getSharedPreferences("auto-login", Context.MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();

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

            String username = sp.getString("username", "");

            db.collection("users").document(username).get().addOnCompleteListener(task -> {

                if(task.isSuccessful()){

                    DocumentSnapshot document = task.getResult();

                    long reward = (long) document.get("reward");

                    TextView textViewPriceValue = view.findViewById(R.id.textView_price_value);
                    String priceValue = textViewPriceValue.getText().toString();
                    double price = Double.parseDouble(priceValue.split(" ")[0]);

                    price = price - price * reward / 100;

                    float balance = sp.getFloat("balance", 0);

                    addToPayments(park.getName(), String.valueOf(price));
                    updateBalance((float) (balance - price));
                    increaseCoins();

                    db.collection("users").document(username).update("reward", 0);

                    dismiss();

                }

            });


        });

    }

    private void updateBalance(float new_balance) {

        SharedPreferences.Editor editor = sp.edit();

        String username = sp.getString("username", "");

        editor.putFloat("balance", new_balance);

        editor.apply();

        db.collection("users").document(username).update("balance", new_balance);

    }

    private void increaseCoins() {

        String username = sp.getString("username", "");

        int coins = sp.getInt("coins", 0) + 1;

        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("coins", coins);
        editor.apply();

        db.collection("users").document(username).update("coins", coins);

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


}
