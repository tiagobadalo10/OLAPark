package com.example.olapark.nav.parks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.olapark.R;
import com.google.android.material.slider.Slider;

public class FilterDialog extends DialogFragment {

    private View view;
    private MyDialogListener listener;

    private SharedPreferences sp;

    public static FilterDialog newInstance(String title) {
        FilterDialog yourDialogFragment = new FilterDialog();

        Bundle args = new Bundle();
        args.putString("title", title);
        yourDialogFragment.setArguments(args);

        return yourDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_filter_dialog, container, false);

        sp = getActivity().getSharedPreferences("filters", Context.MODE_PRIVATE);

        configureImageButton();

        return view;
    }

    private void configureImageButton() {
        Button btn_apply = view.findViewById(R.id.directions_button);
        Button btn_clear = view.findViewById(R.id.reserve_button);

        Slider slider = view.findViewById(R.id.slider);

        Spinner spinner = view.findViewById(R.id.spinner);
        String[] items = new String[]{"All", "Low", "Medium", "High"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);

        Switch switch_coverage = view.findViewById(R.id.coverage_switch);


        // If isn't empty, change filters
        if(!sp.getAll().isEmpty()){
            float range = sp.getFloat("range", 0);
            String occupation = sp.getString("occupation", "");
            boolean coverage = sp.getBoolean("coverage", false);

            slider.setValue(range);

            int spinnerPos = adapter.getPosition(occupation);
            spinner.setSelection(spinnerPos);

            switch_coverage.setChecked(coverage);
        }


        btn_apply.setOnClickListener(v -> {
            String spinnerValue = spinner.getSelectedItem().toString().toUpperCase();
            Occupation occupation = null;

            if (!spinnerValue.equals("ALL")) {
                occupation = Occupation.valueOf(spinnerValue);
            }

            Switch privacy_switch = view.findViewById(R.id.coverage_switch);

            boolean coverage = privacy_switch.isChecked();

            FilterOptions filterOptions;

            if(coverage){
                filterOptions = new FilterOptions(slider.getValue(), occupation, true);
            }
            else{
                filterOptions = new FilterOptions(slider.getValue(), occupation, false);
            }

            // Save filters

            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat("range", slider.getValue());
            editor.putString("occupation", spinner.getSelectedItem().toString());
            editor.putBoolean("coverage", coverage);

            editor.commit();

            listener.setFilter(filterOptions);
            dismiss();
        });

        btn_clear.setOnClickListener(v -> {

            FilterOptions filterOptions = new FilterOptions(0, null, false);

            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat("range", 0);
            editor.putString("occupation", "");
            editor.putBoolean("coverage", false);
            editor.commit();

            listener.setFilter(filterOptions);

            dismiss();
        });
    }

    public void setListener(MyDialogListener listener) {
        this.listener = listener;
    }

    public interface MyDialogListener{
        void setFilter(FilterOptions filterOptions);
    }

}
