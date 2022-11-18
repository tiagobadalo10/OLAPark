package com.example.olapark.ui.settings;

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
import com.example.olapark.databinding.FragmentSettingsBinding;
import com.example.olapark.ui.parks.ParksViewModel;
import com.example.olapark.ui.payments.PaymentsFragment;
import com.example.olapark.ui.payments.PaymentsViewModel;
import com.example.olapark.ui.rewards.RewardsViewModel;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}