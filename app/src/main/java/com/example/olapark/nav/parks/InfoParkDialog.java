package com.example.olapark.nav.parks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.olapark.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class InfoParkDialog extends DialogFragment {

    private View view;
    private Park park;
    private GoogleMap map;
    private LatLng currentPosition;

    private final String url = "https://roads.googleapis.com/v1/snapToRoads?interpolate=true&path=";
    private final String key = "AIzaSyBx64LbDqZGT7otVA_QFu_QHJAHeA7A8kQ";
    private MapsFragment mapFragment;

    public static InfoParkDialog newInstance(String title) {
        InfoParkDialog yourDialogFragment = new InfoParkDialog();

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
        createReport();
        getDirections();

        return view;
    }

    private void createReport() {

        Button report = view.findViewById(R.id.report_button);
        report.setOnClickListener(v -> {
            dismiss();
            ReportFragment dialog = ReportFragment.newInstance("Report");
            dialog.show(getFragmentManager().beginTransaction(), "dialog");

        });
    }

    private void getDirections() {

        Button directions = view.findViewById(R.id.directions_button);
        directions.setOnClickListener(v -> {

            Toast.makeText(getContext(), "helloooo", Toast.LENGTH_SHORT).show();

            this.mapFragment.direction(this.park.getLocation());
            dismiss();

        });


    }

    public void setPark(Park park) {
        if (park == null) {
            dismiss();
        }
        this.park = park;
    }

    public void setMap(GoogleMap mMap) {
        this.map = mMap;
    }

    public void setMapFragment(MapsFragment fragment) {this.mapFragment = fragment;}

    private void configureImageButton() {

        TextView name = view.findViewById(R.id.name);
        TextView occupation = view.findViewById(R.id.occupation);
        TextView pricePerHour = view.findViewById(R.id.pricePerHour);
        TextView coverage = view.findViewById(R.id.coverage);

        name.setText(park.getName());
        occupation.setText(park.getOccupation().toString());
        pricePerHour.setText(park.getPricePerHour() + "€");
        coverage.setText(String.valueOf(park.getCoverage()));

    }

    public void setCurrentPosition(LatLng currentPosition) {

        this.currentPosition = currentPosition;

    }

}


