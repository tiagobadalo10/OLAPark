package com.example.olapark.ui.payments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.olapark.databinding.FragmentPaymentsBinding;
import com.example.olapark.ui.parks.ParksViewModel;

public class PaymentsFragment extends Fragment {

    private FragmentPaymentsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PaymentsViewModel paymentsViewModel =
                new ViewModelProvider(this).get(PaymentsViewModel.class);

        binding = FragmentPaymentsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}