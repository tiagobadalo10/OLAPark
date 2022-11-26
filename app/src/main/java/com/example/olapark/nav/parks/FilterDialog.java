package com.example.olapark.nav.parks;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.olapark.R;
import com.google.android.material.slider.Slider;

import java.util.Locale;

public class FilterDialog extends DialogFragment {

    private View view;
    private MyDialogListener listener;

    public static FilterDialog newInstance(String title) {
        FilterDialog yourDialogFragment = new FilterDialog();

        //example of passing args
        Bundle args = new Bundle();
        args.putString("title", title);
        yourDialogFragment.setArguments(args);

        return yourDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_filter_dialog, container, false);

        configureImageButton();

        return view;
    }

    private void configureImageButton() {
        Button btn_apply = (Button) view.findViewById(R.id.apply_button);
        Button btn_clear = (Button) view.findViewById(R.id.clear_button);

        Slider slider = (Slider) view.findViewById(R.id.slider);

        //Drop-down List
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        String[] items = new String[]{"All", "Low", "Medium", "High"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);

        btn_apply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String spinnerValue = spinner.getSelectedItem().toString().toUpperCase();
                Occupation occupation = null;

                if (!spinnerValue.equals("ALL")) {
                    occupation = Occupation.valueOf(spinnerValue);
                }

                FilterOptions filterOptions = new FilterOptions(slider.getValue(), occupation);
                listener.setFiter(filterOptions);
                dismiss();
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setListener(MyDialogListener listener) {
        this.listener = listener;
    }

    public interface MyDialogListener{
        void setFiter(FilterOptions filterOptions);
    }

}
