package com.example.olapark.nav.parks;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.olapark.ActivityTransitionReceiver;
import com.example.olapark.R;
import com.example.olapark.ReportActivity;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class InfoParkDialog extends DialogFragment {

    private View view;
    private Park park;
    private GoogleMap map;
    private LatLng currentPosition;

    private String url = "https://roads.googleapis.com/v1/snapToRoads?interpolate=true&path=";
    private String key = "AIzaSyBx64LbDqZGT7otVA_QFu_QHJAHeA7A8kQ";
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

            Intent i = new Intent(getActivity(), ReportActivity.class);
            startActivity(i);
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

        TextView occupation = view.findViewById(R.id.occupation);
        TextView pricePerHour = view.findViewById(R.id.pricePerHour);
        TextView name = view.findViewById(R.id.name);

        occupation.setText(park.getOccupation().toString());
        pricePerHour.setText(park.getPricePerHour() + "€");
        name.setText(park.getName());
    }

    public void setCurrentPosition(LatLng currentPosition) {

        this.currentPosition = currentPosition;

    }

}


