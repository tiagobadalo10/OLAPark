package com.example.olapark.nav.parks;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.olapark.R;

public class ReportFragment extends DialogFragment {

    private View v;

    public static ReportFragment newInstance(String title) {
        ReportFragment yourDialogFragment = new ReportFragment();

        Bundle args = new Bundle();
        args.putString("title", title);
        yourDialogFragment.setArguments(args);

        return yourDialogFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_report, container, false);

        Spinner spinner = v.findViewById(R.id.report_options);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.report_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        submitReport();

        return v;
    }

    private void submitReport() {

        ImageView submit_report = v.findViewById(R.id.report_submit);

        submit_report.setOnClickListener(view -> {

            Toast.makeText(getContext(), "Reporte concluído", Toast.LENGTH_SHORT).show();

            dismiss();

        });

    }
}