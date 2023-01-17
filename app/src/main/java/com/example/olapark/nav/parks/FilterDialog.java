package com.example.olapark.nav.parks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.olapark.R;
import com.google.android.material.slider.Slider;

public class FilterDialog extends DialogFragment {

    private View view;
    private MyDialogListener listener;

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

        configureImageButton();

        return view;
    }

    private void configureImageButton() {
        Button btn_apply = view.findViewById(R.id.directions_button);
        Button btn_clear = view.findViewById(R.id.reserve_button);

        Slider slider = view.findViewById(R.id.slider);

        //Drop-down List
        Spinner spinner = view.findViewById(R.id.spinner);
        String[] items = new String[]{"All", "Low", "Medium", "High"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);

        btn_apply.setOnClickListener(v -> {
            String spinnerValue = spinner.getSelectedItem().toString().toUpperCase();
            Occupation occupation = null;

            if (!spinnerValue.equals("ALL")) {
                occupation = Occupation.valueOf(spinnerValue);
            }

            FilterOptions filterOptions = new FilterOptions(slider.getValue(), occupation);
            listener.setFilter(filterOptions);
            dismiss();
        });

        btn_clear.setOnClickListener(v -> dismiss());
    }

    public void setListener(MyDialogListener listener) {
        this.listener = listener;
    }

    public interface MyDialogListener{
        void setFilter(FilterOptions filterOptions);
    }

}
