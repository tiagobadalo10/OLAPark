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
                    if (activity.getConfidence() > 75) {
                        Toast.makeText(context, "O utilizador está a conduzir"
                                , Toast.LENGTH_SHORT).show();
                        if (service != null)
                            service.isDriving();
                    }
                    break;
                }
                case DetectedActivity.WALKING:
                case DetectedActivity.STILL: {
                    break;
                }
            }
        }
    }

}
