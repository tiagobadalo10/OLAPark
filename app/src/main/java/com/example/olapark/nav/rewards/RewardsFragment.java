package com.example.olapark.nav.rewards;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RewardsFragment extends Fragment {

    private FragmentRewardsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RewardsViewModel rewardsViewModelViewModel =
                new ViewModelProvider(this).get(RewardsViewModel.class);

        binding = FragmentRewardsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}