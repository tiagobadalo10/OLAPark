package com.example.olapark.nav.parks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.PlacesApi;
import com.google.maps.model.Geometry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    Context mContext;
    private ArrayList<LatLng> latLngList = new ArrayList<>();
    private GoogleMapOptions options;
    private FilterOptions filterOptions;
    private ParkCatalog parks;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currPosition;
    private Polyline polyline = null;

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

        // Initialize parks
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

        FragmentHelper.getInstance().setFragment(this);
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

        options.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(false)
                .rotateGesturesEnabled(false)
                .tiltGesturesEnabled(false);

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

    private void updateCurrentPosition() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            currPosition = new LatLng(location.getLatitude(), location.getLongitude());

        });
    }

    private void checkRaining() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {

            String tempUrl = url + "?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&appid=" + appid;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl,
                    response -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                            String main = jsonObjectWeather.getString("main");
                            // It's raining
                            if ("Rain".equals(main)) {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    System.out::println);

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);

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
            currPosition = new LatLng(location.getLatitude(), location.getLongitude());

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
        dialog.setMap(mMap);
        dialog.setMapFragment(this);

        updateCurrentPosition();
        dialog.setCurrentPosition(currPosition);

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

    public String latLngToString(LatLng latLng) {
        return latLng.latitude+ ", " + latLng.longitude;
    }

    public void direction(LatLng destination){
        if (polyline != null)
            polyline.remove();

        updateCurrentPosition();

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        String url = Uri.parse("https://maps.googleapis.com/maps/api/directions/json")
                .buildUpon()
                .appendQueryParameter("destination", latLngToString(destination))
                .appendQueryParameter("origin", latLngToString(currPosition))
                .appendQueryParameter("mode", "driving")
                .appendQueryParameter("key", "AIzaSyBWzhZkDZHP1UF4ufvLhZ0y50NYK2ROJMY")
                .toString();

        Log.d("coo", url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("OK")) {
                        JSONArray routes = response.getJSONArray("routes");

                        ArrayList<LatLng> points;
                        PolylineOptions polylineOptions = null;

                        for (int i=0;i<routes.length();i++){
                            points = new ArrayList<>();
                            polylineOptions = new PolylineOptions();
                            JSONArray legs = routes.getJSONObject(i).getJSONArray("legs");

                            for (int j=0;j<legs.length();j++){
                                JSONArray steps = legs.getJSONObject(j).getJSONArray("steps");

                                for (int k=0;k<steps.length();k++){
                                    String polyline = steps.getJSONObject(k).getJSONObject("polyline").getString("points");
                                    List<LatLng> list = decodePoly(polyline);

                                    for (int l=0;l<list.size();l++){
                                        LatLng position = new LatLng((list.get(l)).latitude, (list.get(l)).longitude);
                                        points.add(position);
                                    }
                                }
                            }
                            polylineOptions.addAll(points);
                            polylineOptions.width(10);
                            polylineOptions.color(ContextCompat.getColor(getContext(), R.color.black));
                            polylineOptions.geodesic(true);
                        }
                        polyline = mMap.addPolyline(polylineOptions);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(jsonObjectRequest);
    }

    public static List<LatLng> decodePoly(final String polyline) {
        int len = polyline.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = polyline.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = polyline.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }




}