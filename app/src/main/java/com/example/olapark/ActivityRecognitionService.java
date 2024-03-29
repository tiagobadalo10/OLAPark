package com.example.olapark;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
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
import com.example.olapark.nav.parks.Park;
import com.example.olapark.nav.parks.ParkCatalog;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class ActivityRecognitionService extends Service {

    private static final int GEOFENCE_READIUS = 100;
    private static final int NOTIF_ID = 1001;
    private static final String CHANNEL_ID = "Activity Recognition Service ID";

    private final boolean runningQOrLater =
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;

    private boolean activityRecognitionEnabled;

    private PendingIntent mActivityRecognitionsPendingIntent;
    private RecognitionReceiver mRecognitionsReceiver;
    private final String ACTIVITY_RECOGNITION_UPDATE =
            BuildConfig.APPLICATION_ID + "ACTIVITY_RECOGNITION_UPDATE";

    private final IBinder mBinder = new LocalBinder();

    private boolean enterInFence = false;
    private boolean isDriving = false;
    private boolean sendNotification = false;

    private ParkCatalog parks;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public ActivityRecognitionService getService() {
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

                        MapsFragment maps = FragmentHelper.getInstance().getFragment();

                        parks = ParkCatalog.getInstance(maps);
                        parks.connectService(getApplicationContext());

                        activityRecognitionEnabled = false;

                        Intent intent2 = new Intent(ACTIVITY_RECOGNITION_UPDATE);
                        mActivityRecognitionsPendingIntent =
                                PendingIntent.getBroadcast(getApplicationContext(), 0, intent2
                                        , 0);

                        mRecognitionsReceiver = new RecognitionReceiver(service);

                        // TODO: Enable/Disable activity tracking and ask for permissions if needed.
                        if (activityRecognitionPermissionApproved()) {
                            enableActivityRecognitions();
                        }

                        // TODO: Register the BroadcastReceiver to listen for activity transitions.
                        registerReceiver(mRecognitionsReceiver, new IntentFilter(ACTIVITY_RECOGNITION_UPDATE));

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

    private void enableActivityRecognitions() {
        // Create a new ActivityRecognitionClient
        ActivityRecognitionClient activityRecognitionClient = ActivityRecognition.getClient(getApplicationContext());

        // Create a PendingIntent that will receive the activity updates
        //Intent intent = new Intent(getApplicationContext(), RecognitionReceiver.class);
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Request activity updates for the IN_VEHICLE activity type
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
        Task<Void> task = activityRecognitionClient.requestActivityUpdates(3000, mActivityRecognitionsPendingIntent);

        task.addOnSuccessListener(
                result -> {
                    Toast.makeText(getApplicationContext(), "Recognition Api was successfully registered."
                            , Toast.LENGTH_SHORT).show();
                    Log.d("Service", "Recognition Api was successfully registered.");
                    activityRecognitionEnabled = true;
                });

        task.addOnFailureListener(
                e -> Toast.makeText(getApplicationContext(),
                        "Recognition Api could NOT be registered."
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

        SharedPreferences sh = getSharedPreferences("service", MODE_PRIVATE);
        SharedPreferences.Editor editor = sh.edit();
        editor.putString("parkLocation", lat + " " + lng);
        editor.apply();

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
        this.isDriving = false;
        this.enterInFence = false;
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
        if (maps == null)
            return false;
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
            editor.commit();

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

    public void setGeofence() {

        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(getApplicationContext());
        List<Geofence> geofenceList = new ArrayList<>();

        for(Park park : parks.getParks()) {
            LatLng location = park.getLocation();
            geofenceList.add(new Geofence.Builder()
                    .setRequestId(park.getName())
                    .setCircularRegion(location.latitude, location.longitude
                            , GEOFENCE_READIUS)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build());
        }

        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(geofenceList)
                .build();

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        geofencingClient.addGeofences(geofencingRequest, getGeofencePendingIntent())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }
}
