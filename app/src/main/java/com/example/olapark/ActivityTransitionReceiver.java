package com.example.olapark;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;

public class ActivityTransitionReceiver extends BroadcastReceiver {

    public static final String TRANSITION_ACTION_RECEIVER =
            BuildConfig.APPLICATION_ID + "TRANSITION_ACTION_RECEIVER";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (TRANSITION_ACTION_RECEIVER == intent.getAction()) {
            Log.d("DetectActivityReceiver", "Received an unsupported action.");
            return;
        }

        if (ActivityTransitionResult.hasResult(intent)){
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            for (ActivityTransitionEvent event : result.getTransitionEvents()){
                System.out.println(event.toString());
            }
        }
    }
}
