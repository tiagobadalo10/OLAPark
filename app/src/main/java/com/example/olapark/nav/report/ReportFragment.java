package com.example.olapark.nav.report;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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