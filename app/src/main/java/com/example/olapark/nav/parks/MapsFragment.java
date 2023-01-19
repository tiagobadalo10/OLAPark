package com.example.olapark.nav.parks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.olapark.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    Context mContext;
    private GoogleMapOptions options;
    private ParkCatalog parks;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currPosition;
    private Polyline polyline = null;
    private View v;
    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String appid = "8ff90c2810d99aea0486ad49724d792a";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_maps, container, false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        options = new GoogleMapOptions();
        SupportMapFragment.newInstance(options);

        FragmentHelper.getInstance().setFragment(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SharedPreferences sp = getActivity().getSharedPreferences("filters", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("mmap", mMap.toString());

        options.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(false)
                .rotateGesturesEnabled(false)
                .tiltGesturesEnabled(false);

        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MapsFragment", "nao tem permissoes");
            return;
        }
        mMap.setMyLocationEnabled(true);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(5);
        locationRequest.setInterval(10);
        locationRequest.setFastestInterval(1000);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Handle new location
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                }
            };
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

        checkRaining();

        mMap.setOnMarkerClickListener(marker -> {
            if (marker == null) {
                return true;
            }

            openInfoDialog(parks.findParkByLocation(marker.getPosition()));
            return false;
        });

        parks = ParkCatalog.getInstance(this);
        parks.setParksFragment(this);
        parks.setParksCatalog();
    }


    private void updateCurrentPosition() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> currPosition = new LatLng(location.getLatitude(), location.getLongitude()));
    }
    private void checkRaining() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {

            if(location == null)
                return;

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
                                RainingFragment dialog = RainingFragment.newInstance("Raining");
                                dialog.show(getFragmentManager().beginTransaction(), "dialog");
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

    public void setParkMarkers() {
        if (mMap == null) {
            return;
        }
        for (Park park : parks.getParks()) {
            Log.d("parques", park.getName());
            mMap.addMarker(new MarkerOptions()
                    .position(park.getLocation())
                    .title(park.getName())
                    .snippet("Occupation level: " + park.getOccupation())
                    .icon(BitmapDescriptorFactory.defaultMarker(park.getMarkerColor())));
        }
    }

    public void setParksMarkersWithFilter(FilterOptions filterOptions) {

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
    private void openInfoDialog(Park park) {
        InfoParkDialog dialog = InfoParkDialog.newInstance("Info");
        dialog.setPark(park);
        dialog.setMap(mMap);
        dialog.setMapFragment(this);

        updateCurrentPosition();
        dialog.setCurrentPosition(currPosition);

        dialog.show(getFragmentManager().beginTransaction(), "dialog");
    }

    public void openOccupationDialog() {
        OccupationParkDialog dialog = OccupationParkDialog.newInstance("Occupation");

        SharedPreferences sh = getActivity().getSharedPreferences("service", Context.MODE_PRIVATE);
        String location = sh.getString("parkLocation", null);

        if (location == null) {
            return;
        }

        double lat = Double.valueOf(location.split(" ")[0]);
        double lng = Double.valueOf(location.split(" ")[1]);

        Park park = parks.findParkByLocation(new LatLng(lat, lng));
        Log.d("park", new LatLng(lat, lng).toString());
        dialog.setPark(park);

        dialog.show(getFragmentManager().beginTransaction(), "dialog");
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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
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
        }, error -> {

        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(jsonObjectRequest);
    }

    public static List<LatLng> decodePoly(final String polyline) {
        int len = polyline.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<>();
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

    @SuppressLint("RestrictedApi")
    @Override
    public void onPause() {

        super.onPause();
    }
}