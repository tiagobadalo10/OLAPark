package com.example.olapark.nav.parks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.olapark.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class OccupationParkDialog extends DialogFragment {

    private View view;
    private Park park;
    private GoogleMap map;
    private LatLng currentPosition;

    private final String url = "https://roads.googleapis.com/v1/snapToRoads?interpolate=true&path=";
    private final String key = "AIzaSyBx64LbDqZGT7otVA_QFu_QHJAHeA7A8kQ";
    private MapsFragment mapFragment;

    public static OccupationParkDialog newInstance(String title) {
        OccupationParkDialog yourDialogFragment = new OccupationParkDialog();

        Bundle args = new Bundle();
        args.putString("title", title);
        yourDialogFragment.setArguments(args);

        return yourDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.layout_occupation_park_dialog, container, false);

        submit();
        cancel();

        return view;
    }

    private void submit() {

        Button report = view.findViewById(R.id.submit_button);
        report.setOnClickListener(v -> {
            dismiss();
        });
    }

    private void cancel() {

        Button report = view.findViewById(R.id.cancel_button);
        report.setOnClickListener(v -> {
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


    public void setCurrentPosition(LatLng currentPosition) {

        this.currentPosition = currentPosition;

    }

}
