package com.example.olapark;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceTransitionsIntentService extends IntentService {

    private ActivityRecognitionService service = null;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ActivityRecognitionService.LocalBinder binder = (ActivityRecognitionService.LocalBinder) iBinder;
            service = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();

        Intent serviceIntent = new Intent(this, ActivityRecognitionService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            // trate o erro aqui
            return;
        }

        while(service == null) {}

        // Obtenha o tipo de transição (entrar ou sair)
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            // processa as informações sobre as geofences acionadas aqui
            Toast.makeText(getApplicationContext(), "entrou", Toast.LENGTH_SHORT).show();
            service.sendNotificationTransitions("Entrou na fence");
            service.enterInFence(triggeringGeofences.get(0).getLongitude(), triggeringGeofences.get(0).getLatitude());
        }
    }
}
