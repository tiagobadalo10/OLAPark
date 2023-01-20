package com.example.olapark;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TransitionsReceiver extends BroadcastReceiver {

    private final ActivityRecognitionService service;
    private boolean isDriving = false;
    private boolean isParked = false;

    public TransitionsReceiver(ActivityRecognitionService service) {
        this.service = service;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ActivityTransitionResult.hasResult(intent)) {

            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);

            for (ActivityTransitionEvent event : result.getTransitionEvents()) {

                String info = "Transition: " + toActivityString(event.getActivityType()) +
                        " (" + toTransitionType(event.getTransitionType()) + ")" + "   " +
                        new SimpleDateFormat("HH:mm:ss", Locale.US).format(new Date());

                service.sendNotificationTransitions(info);

                //TODO detetar que esta a conduzir e que esta parado num parque proximo

                if (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                    if (event.getActivityType() == DetectedActivity.WALKING || //TODO mudar para driving
                            event.getActivityType() == DetectedActivity.UNKNOWN) {
                        isDriving = true;
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
