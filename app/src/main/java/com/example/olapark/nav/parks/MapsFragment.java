package com.example.olapark.nav.parks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.olapark.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    Context mContext;
    private ArrayList<LatLng> latLngList = new ArrayList<>();
    private GoogleMapOptions options;
    private FilterOptions filterOptions;
    private ParkCatalog parks;
    private FusedLocationProviderClient fusedLocationClient;

    private String url = "https://api.openweathermap.org/data/2.5/weather";
    private String appid = "8ff90c2810d99aea0486ad49724d792a";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        //initialize parks
        parks = new ParkCatalog();
        parks.setParksCatalog();

        this.subscribeListener();

        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        options = new GoogleMapOptions();
        mapFragment.newInstance(options);
    }

    private void isLocationEnabled() {

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.");
            alertDialog.setPositiveButton("Location Settings", (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            });
            alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            AlertDialog alert = alertDialog.create();
            alert.show();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
                .compassEnabled(false)
                .rotateGesturesEnabled(false)
                .tiltGesturesEnabled(false);

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        checkRaining();

        setParkMarkers(parks);

        mMap.setOnMarkerClickListener(marker -> {
            if (marker == null) {
                return true;
            }
            String markerName = marker.getTitle();
            Toast.makeText(getContext(), "Clicked location is " + marker.getPosition(), Toast.LENGTH_SHORT).show();
            openDialog(parks.findParkByLocation(marker.getPosition()));
            return false;
        });
    }

    private void checkRaining() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {

            String tempUrl = url + "?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&appid=" + appid;
            System.out.println(tempUrl);
            new StringRequest(Request.Method.POST, tempUrl,
                    response -> System.out.println(response),
                    error -> System.out.println("error")

            );

        });



    }

    public void setParkMarkers(ParkCatalog parks) {
        for (Park park : parks) {
            mMap.addMarker(new MarkerOptions()
                    .position(park.getLocation())
                    .title(park.getName())
                    .snippet("Occupation level: " + park.getOccupation())
                    .icon(BitmapDescriptorFactory.defaultMarker(park.getMarkerColor())));
        }
    }

    public void setParksMarkersWithFilter(FilterOptions filterOptions) {
        Toast.makeText(getContext(), "mapsFragement", Toast.LENGTH_SHORT).show();

        mMap.clear();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            LatLng currPosition = new LatLng(location.getLatitude(), location.getLongitude());

            for (Park park : parks.filterParks(filterOptions, currPosition)) {
                mMap.addMarker(new MarkerOptions()
                        .position(park.getLocation())
                        .title(park.getName())
                        .snippet("Occupation level: " + park.getOccupation())
                        .icon(BitmapDescriptorFactory.defaultMarker(park.getMarkerColor())));
            }
        });
    }

    private void openDialog(Park park) {
        InfoParkDialog dialog = InfoParkDialog.newInstance("Info");
        dialog.setPark(park);
        dialog.show(getFragmentManager().beginTransaction(), "dialog");
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        String msg = "New Latitude: " + latitude + "New Longitude: " + longitude;
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();

        LatLng curr = new LatLng(latitude, longitude);
        this.latLngList.add(curr);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curr, 13));
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    public void unsubscribeListener() {

        this.locationManager.removeUpdates(this);
    }

    public void subscribeListener() {

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext, "ola", Toast.LENGTH_LONG).show();
            return;
        }
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                2000,
                20, this);
        isLocationEnabled();
    }

    public ParkCatalog getParkCatalog() {
        return this.parks;
    }

}