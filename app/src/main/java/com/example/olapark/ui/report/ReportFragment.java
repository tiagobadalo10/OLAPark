package com.example.olapark.ui.report;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.olapark.databinding.FragmentReportBinding;
import com.example.olapark.ui.parks.ParksViewModel;
import com.example.olapark.ui.payments.PaymentsFragment;
import com.example.olapark.ui.payments.PaymentsViewModel;

public class ReportFragment extends Fragment {

    private FragmentReportBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ReportViewModel reportViewModel =
                new ViewModelProvider(this).get(ReportViewModel.class);

        binding = FragmentReportBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}