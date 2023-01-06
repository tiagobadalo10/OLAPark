package com.example.olapark;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.olapark.databinding.ActivityMainMenuBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;



public class MainMenuActivity extends AppCompatActivity implements SensorEventListener {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseStorage fs;
    private StorageReference profileRef;
    private SensorManager sensorManager;

    final long MEGA_BYTE = 1024*1024;


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
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        SharedPreferences sp = getSharedPreferences("auto-login", MODE_PRIVATE);
        if(sp.contains("username"))
            updateUsername(navigationView, (String) sp.getAll().get("username"));

        profileRef = fs.getReference(sp.getAll().get("username") + "/profilepicture.jpeg");

        loadProfilePicture();

        changeToProfile(navigationView);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_LIGHT){
            int light = (int) event.values[0];

            Window window = getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();

            if(light >= 0 && light <= 11){
                layoutParams.screenBrightness = 255 / 255f;
            }

            window.setAttributes(layoutParams);
        }
        else if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            if(x > 3 || x < -3 || y > 10 || y < -10 || z > 3 || z < -3){

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
        if(lightSensor != null){
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        Sensor accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if(accelerationSensor != null){
            sensorManager.registerListener(this, accelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
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


    public void changeToProfile(NavigationView navigationView){
        View view = navigationView.getHeaderView(0);
        AppCompatImageView nav_user = view.findViewById(R.id.profile_picture);
        nav_user.setOnClickListener(v -> {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
        });
    }

    public void updateUsername(NavigationView navigationView, String username){
        View view = navigationView.getHeaderView(0);
        TextView nav_username = view.findViewById(R.id.app_username);
        nav_username.setText(username);
    }
}