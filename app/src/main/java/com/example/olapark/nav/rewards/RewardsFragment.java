package com.example.olapark.nav.rewards;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.olapark.R;
import com.example.olapark.databinding.FragmentRewardsBinding;
import com.google.firebase.firestore.FirebaseFirestore;


public class RewardsFragment extends Fragment {

    private FragmentRewardsBinding binding;

    private FirebaseFirestore db;

    private SharedPreferences sp;

    private View v;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRewardsBinding.inflate(inflater, container, false);

        db = FirebaseFirestore.getInstance();
        sp = getActivity().getSharedPreferences("auto-login", MODE_PRIVATE);

        v = binding.getRoot();

        updateCoins();

        return v;
    }

    private void updateCoins() {

        int coins = sp.getInt("coins", 0);

        TextView number_coins = v.findViewById(R.id.coins);

        number_coins.setText(String.valueOf(coins));

    }

}