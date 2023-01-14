package com.example.olapark;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Handler;
import android.util.Log;


import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sp;
    private FirebaseFirestore db;

    private PendingIntent mPendingIntent;
    private ActivityTransitionReceiver mTransitionsReceiver;

    private PendingIntent pendingIntent;
    private BroadcastReceiver broadcastReceiver;
    private ActivityRecognitionClient activityRecognitionClient;
    private boolean wasDriving = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        setSettings();

        requestTransactionsUpdates();

        new Handler().postDelayed(() -> {
            Intent i;
            sp = getSharedPreferences("auto-login", MODE_PRIVATE);
            // Verify if there is an logged account
            if (sp.contains("username")) {
                i = new Intent(MainActivity.this, MainMenuActivity.class);
            }
            // If there isn't, change to login activity
            else {
                i = new Intent(MainActivity.this, LoginActivity.class);

            }

            startActivity(i);
            finish();

        }, 5000);

    }

    private void requestTransactionsUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }

        List transitions = new ArrayList<>();

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        ActivityTransitionRequest request = new ActivityTransitionRequest(transitions);

        Intent intent = new Intent(ActivityTransitionReceiver.TRANSITION_ACTION_RECEIVER);
        mPendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mTransitionsReceiver = new ActivityTransitionReceiver();
        registerReceiver(mTransitionsReceiver,
                new IntentFilter(ActivityTransitionReceiver.TRANSITION_ACTION_RECEIVER));

        Task<Void> task =
                ActivityRecognition.getClient(this)
                        .requestActivityTransitionUpdates(request, mPendingIntent);
        task.addOnSuccessListener(
                result -> {
                    Log.d("ActivityRecognition", "Transitions Api registered with success");
                });
        task.addOnFailureListener(
                e -> {
                    Log.d("ActivityRecognition", "Transitions Api could NOT be registered ${e.localizedMessage}");
                });

    }


    public void sendWarning() {

        List<ActivityTransition> transitions = new ArrayList<>();

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        ActivityTransitionRequest request = new ActivityTransitionRequest(transitions);

        Intent intent = new Intent(this, TransitionsReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

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
        Task<Void> task = activityRecognitionClient.requestActivityTransitionUpdates(request, pendingIntent);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Activity transition updates were successfully registered
                Log.d("MainActivity", "Successfully registered for activity transitions");
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // Registration failed
                Log.e("MainActivity", "Activity transition updates registration failed: " + e.getLocalizedMessage());
            }
        });

        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra("transitionType")) {
                    int transitionType = intent.getIntExtra("transitionType", -1);
                    if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                        // Device has entered a vehicle
                        wasDriving = true;
                    } else if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        // Device has exited a vehicle
                        if (wasDriving) {
                            // Send event
                            Log.d("MainActivity", "Stopped Driving");
                            wasDriving = false;
                        }
                }
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter("activity_transition"));
    }


    private void setSettings() {

        HashMap<String, Boolean> settings = new HashMap<>();

        db.collection("settings").document("settings").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                DocumentSnapshot document = task.getResult();
                // existing settings
                if (document.exists()) {
                    settings.put("auto-payment", document.getBoolean("auto-payment"));
                }
                // default settings
                else {
                    settings.put("auto-payment", false);
                }

                saveSettingsSP(settings);
            }
        });


    }

    private void saveSettingsSP(HashMap<String, Boolean> settings) {

        sp = getSharedPreferences("settings", MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            settings.forEach((key, value) -> {
                editor.putBoolean(key, value);
            });
        }

        editor.commit();
    }

}