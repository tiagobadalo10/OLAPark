package com.example.olapark;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
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


import de.hdodenhof.circleimageview.CircleImageView;

public class MainMenuActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseStorage fs;
    private StorageReference profileRef;

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

        SharedPreferences sp = getSharedPreferences("auto-login", MODE_PRIVATE);
        if(sp.contains("username"))
            updateUsername(navigationView, (String) sp.getAll().get("username"));

        profileRef = fs.getReference(sp.getAll().get("username") + "/profilepicture.jpeg");

        loadProfilePicture();

        changeToProfile(navigationView);
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