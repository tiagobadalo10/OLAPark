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
import android.widget.TextView;

import com.example.olapark.LoginActivity;
import com.example.olapark.R;
import com.example.olapark.databinding.FragmentSettingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


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
        sp = getActivity().getSharedPreferences("auto-login", MODE_PRIVATE);

        deleteAccount();

        return view;
    }

    private void deleteAccount() {

        TextView delete_account = view.findViewById(R.id.delete_account);

        delete_account.setOnClickListener(v -> {

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
}