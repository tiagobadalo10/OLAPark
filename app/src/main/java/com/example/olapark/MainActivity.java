package com.example.olapark;

import android.Manifest;
import android.app.PendingIntent;
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
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    private SharedPreferences sp;
    private FirebaseFirestore db;

    private PendingIntent mPendingIntent;
    private ActivityTransitionReceiver mTransitionsReceiver;

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