package com.example.olapark;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import com.example.olapark.nav.parks.FragmentHelper;
import com.example.olapark.nav.parks.MapsFragment;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;
import java.util.List;

public class ActivityRecognitionService extends Service {

    private static final int NOTIF_ID = 1001;
    private static final String CHANNEL_ID = "Activity Recognition Service ID";

    private final boolean runningQOrLater =
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;

    private boolean activityTrackingEnabled;

    private List<ActivityTransition> activityTransitionList;

    private PendingIntent mActivityTransitionsPendingIntent;
    private TransitionsReceiver mTransitionsReceiver;
    private static final int REQUEST_ACTIVITY_RECOGNITION = 45;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    private final String TRANSITIONS_RECEIVER_ACTION =
            BuildConfig.APPLICATION_ID + "TRANSITIONS_RECEIVER_ACTION";

    private final IBinder mBinder = new LocalBinder();

    private boolean enterInFence = false;
    private boolean isDriving = false;
    private boolean sendNotification = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        ActivityRecognitionService getService() {
            return ActivityRecognitionService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ActivityRecognitionService service = this;

            Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        activityTrackingEnabled = false;

                        // List of activity transitions to track.
                        activityTransitionList = new ArrayList<>();

                        // TODO: Add activity transitions to track.
                        activityTransitionList.add(new ActivityTransition.Builder()
                                .setActivityType(DetectedActivity.IN_VEHICLE)
                                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                                .build());
                        activityTransitionList.add(new ActivityTransition.Builder()
                                .setActivityType(DetectedActivity.IN_VEHICLE)
                                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                                .build());
                        activityTransitionList.add(new ActivityTransition.Builder()
                                .setActivityType(DetectedActivity.WALKING)
                                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                                .build());
                        activityTransitionList.add(new ActivityTransition.Builder()
                                .setActivityType(DetectedActivity.WALKING)
                                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                                .build());
                        activityTransitionList.add(new ActivityTransition.Builder()
                                .setActivityType(DetectedActivity.STILL)
                                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                                .build());
                        activityTransitionList.add(new ActivityTransition.Builder()
                                .setActivityType(DetectedActivity.STILL)
                                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                                .build());

                        // TODO: Initialize PendingIntent that will be triggered when a activity transition occurs.
                        Intent intent1 = new Intent(TRANSITIONS_RECEIVER_ACTION);
                        mActivityTransitionsPendingIntent =
                                PendingIntent.getBroadcast(getApplicationContext(), 0, intent1
                                        , 0);

                        // TODO: Create a BroadcastReceiver to listen for activity transitions.
                        // The receiver listens for the PendingIntent above that is triggered by the system when an
                        // activity transition occurs.
                        mTransitionsReceiver = new TransitionsReceiver(service);

                        // TODO: Enable/Disable activity tracking and ask for permissions if needed.
                        if (activityRecognitionPermissionApproved()) {
                            enableActivityTransitions();
                        }

                        // TODO: Register the BroadcastReceiver to listen for activity transitions.
                        registerReceiver(mTransitionsReceiver, new IntentFilter(TRANSITIONS_RECEIVER_ACTION));


                        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(getApplicationContext());
                        List<Geofence> geofenceList = new ArrayList<>();
                        geofenceList.add(new Geofence.Builder()
                                .setRequestId("area1")
                                .setCircularRegion(38.69770840269444, -9.2930477191839, 2000)
                                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                                .build());

                        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                                .addGeofences(geofenceList)
                                .build();

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }

                        geofencingClient.addGeofences(geofencingRequest, getGeofencePendingIntent())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Service", "Geofencing Api was successfully registered.");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Service", "Geofencing Api was unsuccessfully registered." + e);
                                    }
                                });


                        //ciclo while
                        while (true) {
                            Log.e("Service", "Service is running");
                            if (mapsFragmentIsVisible()) {
                                Log.e("Service", "Fragment is visible");
                            }
                            if (mapsFragmentIsVisible() && sendNotification) {

                            }

                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_LOW
            );
            getSystemService(NotificationManager.class).createNotificationChannel(channel);

            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentText("Service is running")
                    .setContentTitle("Service Enabled")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.logo);

            startForeground(NOTIF_ID, notification.build());
        }

        thread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(getApplicationContext(), GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void disableActivityTransitions() {
        // TODO: Stop listening for activity changes.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        ActivityRecognition.getClient(this).removeActivityTransitionUpdates(mActivityTransitionsPendingIntent)
                .addOnSuccessListener(aVoid -> {
                    activityTrackingEnabled = false;
                    Toast.makeText(getApplicationContext(),
                            "Transitions successfully unregistered.",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(),
                        "Transitions could not be unregistered.",
                        Toast.LENGTH_LONG).show());
    }

    /**
     * Registers callbacks for {@link ActivityTransition} events via a custom
     * {@link BroadcastReceiver}
     */
    private void enableActivityTransitions() {
        // TODO: Create request and listen for activity changes.
        ActivityTransitionRequest request = new ActivityTransitionRequest(activityTransitionList);

        // Register for Transitions Updates.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Void> task = ActivityRecognition.getClient(this)
                .requestActivityTransitionUpdates(request, mActivityTransitionsPendingIntent);

        task.addOnSuccessListener(
                result -> {
                    activityTrackingEnabled = true;
                    Toast.makeText(getApplicationContext(), "Transitions Api was successfully registered."
                            , Toast.LENGTH_SHORT).show();
                    Log.d("Service", "Transitions Api was successfully registered.");
                });

        task.addOnFailureListener(
                e -> Toast.makeText(getApplicationContext(),
                        "Transitions Api could NOT be registered."
                        , Toast.LENGTH_SHORT).show());
    }

    private boolean activityRecognitionPermissionApproved() {

        // TODO: Review permission check for 29+.
        if (runningQOrLater) {
            return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
            );
        } else {
            return true;
        }
    }

    public void sendNotificationTransitions(String info) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_LOW
            );
            getSystemService(NotificationManager.class).createNotificationChannel(channel);

            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentText(info)
                    .setContentTitle("Transitions")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.logo);

            startForeground(NOTIF_ID, notification.build());
        }
    }

    public void enterInFence(double lat, double lng){
        this.enterInFence = true;
        Log.d("lat", String.valueOf(lat) + " " + lng);
        if (this.isDriving) {
            parkingDetected();
        }
    }

    public void isDriving() {
        this.isDriving = true;
        if (this.enterInFence) {
            parkingDetected();
        }
    }

    public void parkingDetected() {
        this.sendNotificationTransitions("OK");
        if (mapsFragmentIsVisible()) {
            MapsFragment maps = FragmentHelper.getInstance().getFragment();
            maps.openOccupationDialog();
        } else {
            sendNotification("Clique para realizar o inquerito de ocupaçao");
        }
    }

    private boolean mapsFragmentIsVisible() {
        MapsFragment maps = FragmentHelper.getInstance().getFragment();
        return maps.isAdded() && maps.isResumed();
    }

    public void sendNotification(String info) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_LOW
            );
            getSystemService(NotificationManager.class).createNotificationChannel(channel);

            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            SharedPreferences sh = getSharedPreferences("service", MODE_PRIVATE);
            SharedPreferences.Editor editor = sh.edit();
            editor.putBoolean("clickNotification", true);
            editor.apply();

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentText(info)
                    .setContentTitle("Parabens Chegou ao seu destino!")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.logo);

            sendNotification = true;
            startForeground(NOTIF_ID, notification.build());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }
}
