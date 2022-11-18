package com.example.olapark.ui.payments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.olapark.R;
import com.example.olapark.databinding.FragmentParksBinding;
import com.example.olapark.databinding.FragmentPaymentsBinding;
import com.example.olapark.ui.parks.ParksViewModel;

public class PaymentsFragment extends Fragment {

    private PaymentsViewModel mViewModel;

    public static PaymentsFragment newInstance() {
        return new PaymentsFragment();
    }

    private FragmentPaymentsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ParksViewModel parksViewModel =
                new ViewModelProvider(this).get(ParksViewModel.class);

        binding = FragmentPaymentsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}