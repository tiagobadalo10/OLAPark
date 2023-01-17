package com.example.olapark;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.olapark.nav.parks.LocationUtils;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TransitionsReceiver extends BroadcastReceiver {

    private final ActivityRecognitionService service;
    private boolean isDriving = false;
    private boolean isParked = false;

    private LocationManager locationManager;
    private Location currentLocation;
    private Context context;

    public TransitionsReceiver(ActivityRecognitionService service) {
        this.service = service;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        if (ActivityTransitionResult.hasResult(intent)) {

            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);

            for (ActivityTransitionEvent event : result.getTransitionEvents()) {

                String info = "Transition: " + toActivityString(event.getActivityType()) +
                        " (" + toTransitionType(event.getTransitionType()) + ")" + "   " +
                        new SimpleDateFormat("HH:mm:ss", Locale.US).format(new Date());

                Log.d("transitions", info);
                Toast.makeText(context, info, Toast.LENGTH_SHORT).show();

                //TODO detetar que esta a conduzir e que esta parado num parque proximo
                if (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                    if (event.getActivityType() == DetectedActivity.WALKING) {
                        isDriving = true;
                        Log.d("isDriving", "isDriving");
                    }
                }

                if (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
                    if (event.getActivityType() == DetectedActivity.WALKING) {
                        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                                        != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        Log.d("currPos", currentLocation.toString());
                        if (LocationUtils.isCloseToTheNearestPark(new LatLng(currentLocation.getLatitude(),
                                        currentLocation.getLongitude()),
                                2000.0)) {
                            isParked = true;
                            Log.d("isParked", "isParked");
                        }
                    }
                }

                if (isDriving && isParked) {
                    isDriving = false;
                    isParked = false;

                    //Intent intentActivity = new Intent(context, MainMenuActivity.class);
                    //intent.putExtra("openPaymentDialog", true);
                    //context.startActivity(intentActivity);

                    Toast.makeText(context, "You have parked", Toast.LENGTH_SHORT).show();
                    Log.d("parked", "You have parked");
                }

                service.sendNotificationTransitions(info);
            }
        }
    }

    private static String toActivityString(int activity) {
        switch (activity) {
            case DetectedActivity.STILL:
                return "STILL";
            case DetectedActivity.WALKING:
                return "WALKING";
            default:
                return "UNKNOWN";
        }
    }

    private static String toTransitionType(int transitionType) {
        switch (transitionType) {
            case ActivityTransition.ACTIVITY_TRANSITION_ENTER:
                return "ENTER";
            case ActivityTransition.ACTIVITY_TRANSITION_EXIT:
                return "EXIT";
            default:
                return "UNKNOWN";
        }
    }

    private boolean checkLocationPermissions() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
    }
}





