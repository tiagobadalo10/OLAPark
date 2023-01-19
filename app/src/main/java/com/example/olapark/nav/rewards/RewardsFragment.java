package com.example.olapark.nav.rewards;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.olapark.R;
import com.example.olapark.databinding.FragmentRewardsBinding;
import com.google.firebase.firestore.DocumentSnapshot;
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

        selectFirstReward();
        selectSecondReward();
        selectThirdReward();

        return v;
    }

    private void selectFirstReward() {

        Button claim_reward = v.findViewById(R.id.first_reward);
        claim_reward.setOnClickListener(view -> {

            String username = sp.getString("username", "");

            db.collection("users").document(username).get().addOnCompleteListener(task -> {

                if(task.isSuccessful()){

                    DocumentSnapshot document = task.getResult();

                    long reward = (long) document.get("reward");

                    if(reward == 0){

                        long coins = (long) document.get("coins");

                        long reward_coins = 5;

                        if(coins >= reward_coins){

                            SharedPreferences.Editor editor = sp.edit();
                            editor.putLong("reward", 25);
                            editor.putLong("coins", coins - reward_coins);
                            editor.apply();

                            db.collection("users").document(username).update("coins", coins - reward_coins);

                            db.collection("users").document(username).update("reward", 25);

                            updateCoins();

                        }
                    }
                }
            });

        });


    }

    private void selectSecondReward() {

        Button claim_reward = v.findViewById(R.id.second_reward);
        claim_reward.setOnClickListener(view -> {

            String username = sp.getString("username", "");

            db.collection("users").document(username).get().addOnCompleteListener(task -> {

                if(task.isSuccessful()){

                    DocumentSnapshot document = task.getResult();

                    long reward = (long) document.get("reward");

                    if(reward == 0){

                        long coins = (long) document.get("coins");

                        long reward_coins = 10;

                        if(coins >= reward_coins){

                            SharedPreferences.Editor editor = sp.edit();
                            editor.putFloat("reward", 50);
                            editor.putLong("coins", coins - reward_coins);
                            editor.apply();


                            db.collection("users").document(username).update("coins", coins - reward_coins);

                            db.collection("users").document(username).update("reward", 50);

                            updateCoins();

                        }
                    }
                }
            });

        });

    }

    private void selectThirdReward() {

        Button claim_reward = v.findViewById(R.id.third_reward);
        claim_reward.setOnClickListener(view -> {

            String username = sp.getString("username", "");

            db.collection("users").document(username).get().addOnCompleteListener(task -> {

                if(task.isSuccessful()){

                    DocumentSnapshot document = task.getResult();

                    long reward = (long) document.get("reward");

                    if(reward == 0){

                        long coins = (long) document.get("coins");

                        long reward_coins = 15;

                        if(coins >= reward_coins){

                            SharedPreferences.Editor editor = sp.edit();
                            editor.putFloat("reward", 100);
                            editor.putLong("coins", coins - reward_coins);
                            editor.apply();


                            db.collection("users").document(username).update("coins", coins - reward_coins);

                            db.collection("users").document(username).update("reward", 100);

                            updateCoins();

                        }
                    }
                }
            });

        });
    }
    private void updateCoins() {

        String username = sp.getString("username", "");

        db.collection("users").document(username).get().addOnCompleteListener(task -> {

            if(task.isSuccessful()){

                DocumentSnapshot document = task.getResult();

                long coins = (long) document.get("coins");

                TextView number_coins = v.findViewById(R.id.coins);

                number_coins.setText(String.valueOf(coins));

            }

        });



    }

}