package com.example.olapark.nav.parks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.olapark.R;
public class RainingFragment extends DialogFragment {

    private View v;

    private static MapsFragment maps;
    private SharedPreferences sp;

    public static RainingFragment newInstance(String title, MapsFragment mapsFragment){
        RainingFragment yourDialogFragment = new RainingFragment();

        maps = mapsFragment;

        Bundle args = new Bundle();
        args.putString("title", title);
        yourDialogFragment.setArguments(args);

        return yourDialogFragment;

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_raining, container, false);

        sp = getActivity().getSharedPreferences("filters", Context.MODE_PRIVATE);

        Button raining_no = v.findViewById(R.id.raining_no);
        raining_no.setOnClickListener(view -> {

            dismiss();

        });

        Button raining_yes = v.findViewById(R.id.raining_yes);
        raining_yes.setOnClickListener(view -> {

            SharedPreferences.Editor editor = sp.edit();

            editor.putFloat("range", 0);
            editor.putString("occupation", "All");
            editor.putBoolean("coverage", true);

            editor.commit();

            maps.setParksMarkersWithFilter(new FilterOptions(0, Occupation.UNKNOWN, true));

            dismiss();

        });


        return v;
    }


}