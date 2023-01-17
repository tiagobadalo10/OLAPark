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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.olapark.nav.parks.LocationUtils;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

public class TransitionsReceiver extends BroadcastReceiver {

    private final ActivityRecognitionService service;
    private boolean isDriving = false;
    private boolean isParked = false;

    private FusedLocationProviderClient fusedLocationClient;
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

                service.sendNotificationTransitions(info);
                Log.d("transitions", info);
                Toast.makeText(context, info, Toast.LENGTH_SHORT).show();

                //TODO detetar que esta a conduzir e que esta parado num parque proximo

                if (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                    if (event.getActivityType() == DetectedActivity.WALKING || //TODO mudar para driving
                            event.getActivityType() == DetectedActivity.UNKNOWN) {
                        isDriving = true;
                        Log.d("isDriving", "isDriving");
                        Toast.makeText(context, "isDriving", Toast.LENGTH_SHORT).show();
                    }
                }

                if (isDriving) {
                    isDriving = false;
                    service.isDriving();
                }
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

}
