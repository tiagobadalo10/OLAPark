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
            // Testing purpose
            String tempUrl = url + currentPosition.latitude + "%2C" + currentPosition.longitude +
                    "%7C" + 38.87736239241292 + "%2C" + -7.168426187583643
                    + "%7C" + 38.883333 + "%2C" + -7.162912 + "&key=" + key;
            System.out.println(tempUrl);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl,
                    response -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray jsonArray = jsonResponse.getJSONArray("snappedPoints");
                            ArrayList<LatLng> points = new ArrayList<>();
                            for (int x = 0; x < jsonArray.length(); x++) {
                                JSONObject jsonObjectLocation = jsonArray.getJSONObject(x);
                                JSONObject jsonLocation = jsonObjectLocation.getJSONObject("location");
                                points.add(new LatLng((Double) jsonLocation.get("latitude"), (Double) jsonLocation.get("longitude")));
                            }

                            PolylineOptions polylineOptions = new PolylineOptions();
                            polylineOptions.addAll(points);
                            map.addPolyline(polylineOptions.color(Color.BLUE));

                            FragmentManager manager = getFragmentManager();
                            FragmentTransaction trans = manager.beginTransaction();
                            trans.remove(this);
                            trans.commit();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    System.out::println);

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);

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


