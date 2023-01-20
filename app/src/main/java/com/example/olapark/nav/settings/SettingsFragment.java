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

import android.widget.Switch;
import android.widget.TextView;

import com.example.olapark.LoginActivity;
import com.example.olapark.R;
import com.example.olapark.databinding.FragmentSettingsBinding;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private View view;
    private FirebaseFirestore db;
    private SharedPreferences sps;
    private SharedPreferences spa;

    private SharedPreferences spf;

    private SharedPreferences spser;

    private SharedPreferences spu;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        view = binding.getRoot();

        db = FirebaseFirestore.getInstance();

        sps = getActivity().getSharedPreferences("settings", MODE_PRIVATE);
        spa = getActivity().getSharedPreferences("auto-login", MODE_PRIVATE);
        spf = getActivity().getSharedPreferences("filters", MODE_PRIVATE);
        spser = getActivity().getSharedPreferences("service", MODE_PRIVATE);
        spu = getActivity().getSharedPreferences("user_pref", MODE_PRIVATE);


        loadSettings();
        deleteAccount();

        return view;
    }

    private void loadSettings() {

        boolean auto_payment;

        auto_payment = sps.getBoolean("auto-payment", false);

        Switch automatic_payment = view.findViewById(R.id.automatic_payment_switch);
        automatic_payment.setChecked(auto_payment);
    }

    private void deleteAccount() {

        TextView delete_account = view.findViewById(R.id.delete_account);

        delete_account.setOnClickListener(v -> {

            FirebaseAuth.getInstance().getCurrentUser().delete();

            String username = spa.getString("username", "");

            db.collection("users").document(username).delete();

            cleanSP();

            Intent i = new Intent(getContext(), LoginActivity.class);
            startActivity(i);

        });
    }

    public void cleanSP(){

        SharedPreferences[] sh = new SharedPreferences[]{sps, spa, spf, spser, spu};

        for(int i = 0; i < sh.length; i++){

            SharedPreferences.Editor editor = sh[i].edit();
            editor.clear();
            editor.apply();

        }

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

        SharedPreferences.Editor editor = sps.edit();
        editor.remove("auto-payment");
        editor.putBoolean("auto-payment", automatic_payment.isChecked());

        editor.apply();

        // save in Firestore Database

        String username = spa.getString("username", "");

        HashMap<String, Object> settings = new HashMap<>();

        settings.put("auto-payment", automatic_payment.isChecked());

        db.collection("users").document(username).update("settings", settings);
    }
}