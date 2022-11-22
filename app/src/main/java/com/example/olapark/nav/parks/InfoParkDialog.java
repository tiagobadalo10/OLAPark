package com.example.olapark.nav.parks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.olapark.R;

public class InfoParkDialog extends DialogFragment {

    private View view;
    private Park park;

    public static InfoParkDialog newInstance(String title) {
        InfoParkDialog yourDialogFragment = new InfoParkDialog();

        //example of passing args
        Bundle args = new Bundle();
        args.putString("title", title);
        yourDialogFragment.setArguments(args);

        return yourDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_info_park_dialog, container, false);

        configureImageButton();

        return view;
    }

    public void setPark(Park park) {
        if (park == null) {
            dismiss();
        }
        this.park = park;
    }

    private void configureImageButton() {

        TextView occupation = (TextView) view.findViewById(R.id.occupation);
        TextView pricePerHour = (TextView) view.findViewById(R.id.pricePerHour);
        TextView name = (TextView) view.findViewById(R.id.name);


        occupation.setText(park.getOccupation().toString());
        pricePerHour.setText(park.getPricePerHour() + "€");
        name.setText(park.getName());
    }

}
