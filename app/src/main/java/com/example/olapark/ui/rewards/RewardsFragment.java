package com.example.olapark.ui.rewards;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.olapark.R;
import com.example.olapark.databinding.FragmentReportBinding;
import com.example.olapark.databinding.FragmentRewardsBinding;
import com.example.olapark.ui.parks.ParksViewModel;
import com.example.olapark.ui.payments.PaymentsFragment;
import com.example.olapark.ui.payments.PaymentsViewModel;

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