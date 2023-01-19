package com.example.olapark;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.olapark.databinding.ActivityMainMenuBinding;
import com.example.olapark.nav.parks.FragmentHelper;
import com.example.olapark.nav.parks.MapsFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MainMenuActivity extends AppCompatActivity implements SensorEventListener {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseStorage fs;
    private FirebaseFirestore db;
    private SharedPreferences sp;
    private StorageReference profileRef;
    private SensorManager sensorManager;
    final long MEGA_BYTE = 1024 * 1024;
    private static final int REQUEST_ACTIVITY_RECOGNITION = 45;
    private final int REQUEST_LOCATION_PERMISSION = 1;

    private boolean locationPermission = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainMenuBinding binding = ActivityMainMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMainMenu.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_parks)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_menu);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        fs = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        sp = getSharedPreferences("auto-login", MODE_PRIVATE);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sp.contains("username")){

            String username = (String) sp.getAll().get("username");

            updateUsername(navigationView, username);

            profileRef = fs.getReference(username + "/profilepicture.jpeg");

            setSettings(username);

        }

        loadProfilePicture();

        SharedPreferences sh = getSharedPreferences("service", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sh.edit();

        if (sh.getBoolean("clickNotification", false)) {
            FragmentHelper.getInstance().setOnFragmentReadyListener(new FragmentHelper.OnFragmentReadyListener() {
                @Override
                public void onFragmentReady() {
                    if (sh.getBoolean("clickNotification", false)) {
                        editor.remove("clickNotification");
                        editor.commit();
                        FragmentHelper.getInstance().getFragment().openOccupationDialog();
                        Toast.makeText(getApplicationContext(), "ola adeus", Toast.LENGTH_SHORT).show();
                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(1001);
                        stopService(new Intent(getApplicationContext(), ActivityRecognitionService.class));
                    }
                }
            });
        }

        changeToProfile(navigationView);

        requestLocationPermission();

    }


    private void setSettings(String username) {

        db.collection("users").document(username).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                DocumentSnapshot document = task.getResult();

                saveSettingsSP((HashMap<String, Boolean>) document.get("settings"));
            }
        });


    }

    private void saveSettingsSP(HashMap<String, Boolean> settings) {

        sp = getSharedPreferences("settings", MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            settings.forEach(editor::putBoolean);
        }

        editor.commit();
    }

    public void startActivityRecognitionService() {
        Intent serviceIntent = new Intent(this, ActivityRecognitionService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        }
    }

    public void requestRecognitionPermission() {
        String[] perms = {Manifest.permission.ACTIVITY_RECOGNITION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            if(!foregroundServiceRunning()){
                startActivityRecognitionService();
            }
            Log.d("permissao_recognition", "tem permissao");
        } else {
            EasyPermissions.requestPermissions(this, "A permissão é necessária para rastrear sua atividade física",
                    REQUEST_ACTIVITY_RECOGNITION, perms);
            Log.d("permissao_recognition", "nao tem permissao");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        String[] perms = {Manifest.permission.ACTIVITY_RECOGNITION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            if(!foregroundServiceRunning()){
                startActivityRecognitionService();
            }
        }
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
            Log.d("permissao_location", "tem permissao");
            if (!locationPermission){
                MapsFragment newInstanceOfFragment = new MapsFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerView, newInstanceOfFragment);
                fragmentTransaction.commit();
            }
            requestRecognitionPermission();
        } else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
            Log.d("permissao_location", "nao tem permissao");
            locationPermission = false;
            requestRecognitionPermission();
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            int light = (int) event.values[0];

            Window window = getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();

            if (light >= 0 && light <= 11) {
                layoutParams.screenBrightness = 255 / 255f;
            }

            window.setAttributes(layoutParams);

        } else if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            if (x > 3 || x < -3 || y > 10 || y < -10 || z > 3 || z < -3) {
                System.out.println("Shaking");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {

        super.onResume();

        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        Sensor accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (accelerationSensor != null) {
            sensorManager.registerListener(this, accelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void loadProfilePicture() {

        profileRef.getBytes(MEGA_BYTE).addOnSuccessListener(bytes -> {

            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            ImageView profile_picture = findViewById(R.id.profile_picture);
            profile_picture.setImageBitmap(bitmap);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_menu);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    public void changeToProfile(NavigationView navigationView) {
        View view = navigationView.getHeaderView(0);
        AppCompatImageView nav_user = view.findViewById(R.id.profile_picture);
        nav_user.setOnClickListener(v -> {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
        });
    }

    public void updateUsername(NavigationView navigationView, String username) {
        View view = navigationView.getHeaderView(0);
        TextView nav_username = view.findViewById(R.id.app_username);
        nav_username.setText(username);
    }
    public boolean foregroundServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){
            if (ActivityRecognitionService.class.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }

}