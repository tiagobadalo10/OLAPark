package com.example.olapark.nav.settings;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.olapark.LoginActivity;
import com.example.olapark.R;
import com.example.olapark.databinding.FragmentSettingsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private View view;
    private FirebaseFirestore db;
    private SharedPreferences sp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        view = binding.getRoot();

        db = FirebaseFirestore.getInstance();

        loadSettings();
        deleteAccount();

        return view;
    }

    private void loadSettings() {

        boolean auto_payment;

        sp = getActivity().getSharedPreferences("settings", MODE_PRIVATE);

        auto_payment = sp.getBoolean("auto-payment", false);

        Switch automatic_payment = view.findViewById(R.id.automatic_payment_switch);
        automatic_payment.setChecked(auto_payment);
    }

    private void deleteAccount() {

        TextView delete_account = view.findViewById(R.id.delete_account);

        delete_account.setOnClickListener(v -> {

            sp = getActivity().getSharedPreferences("auto-login", MODE_PRIVATE);

            FirebaseAuth.getInstance().getCurrentUser().delete();

            String username = sp.getString("username", "");

            db.collection("users").document(username).delete();

            cleanSP();

            Intent i = new Intent(getContext(), LoginActivity.class);
            startActivity(i);

        });
    }

    public void cleanSP(){
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("username");
        editor.remove("password");
        editor.apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Switch automatic_payment = view.findViewById(R.id.automatic_payment_switch);

        // save in Shared Preferences

        SharedPreferences.Editor editor = sp.edit();
        editor.remove("auto-payment");
        editor.putBoolean("auto-payment", automatic_payment.isChecked());

        editor.apply();

        // save in Firestore Database

        db.collection("settings").document("settings").get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    db.collection("settings").document("settings").update("auto-payment", automatic_payment.isChecked());
                }
                else {
                    HashMap<String, Object> settings = new HashMap<>();
                    settings.put("auto-payment", automatic_payment.isChecked());
                    db.collection("settings").document("settings").set(settings);
                }


            }

                });


    }
}