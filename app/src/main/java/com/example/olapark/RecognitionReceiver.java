package com.example.olapark;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.olapark.nav.parks.ParkCatalog;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

public class RecognitionReceiver extends BroadcastReceiver {

    private ActivityRecognitionService service = null;
    private ParkCatalog parks;

    private boolean isDriving = false;
    private boolean isParked = false;


    public RecognitionReceiver() {
        // constructor code here
    }

    public RecognitionReceiver(ActivityRecognitionService service) {
        this.service = service;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities(result.getProbableActivities(), context);
            Log.d("RecognitionReceiver", result.getProbableActivities().toString());
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities, Context context) {
        for (DetectedActivity activity : probableActivities) {
            switch (activity.getType()) {
                case DetectedActivity.IN_VEHICLE: {
                    Toast.makeText(context, "Esta de carro", Toast.LENGTH_LONG);
                    Log.d("RecognitionReceiver", "Esta de carro");
                    break;
                }
                case DetectedActivity.WALKING: {
                    Toast.makeText(context, "Esta a pe", Toast.LENGTH_LONG);
                    break;
                }
                case DetectedActivity.STILL: {
                    Toast.makeText(context, "Esta parado", Toast.LENGTH_LONG);
                    Log.d("RecognitionReceiver", "Esta parado");
                    if (service != null)
                        service.sendNotificationTransitions("Esta parado");
                    break;
                }
            }
        }
    }

}
