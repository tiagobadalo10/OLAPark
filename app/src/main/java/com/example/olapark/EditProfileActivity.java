package com.example.olapark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private String username;
    private String email;
    private Long phone_number;
    private FirebaseFirestore db;
    private SharedPreferences sp;
    private FirebaseStorage fs;
    private StorageReference profileRef;
    final long MEGA_BYTE = 1024*1024;

    private final int CAMERA_REQ_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        db = FirebaseFirestore.getInstance();
        sp = getSharedPreferences("auto-login", MODE_PRIVATE);
        fs = FirebaseStorage.getInstance();

        Bundle user_info = getIntent().getExtras();
        username = user_info.getString("username");
        phone_number = user_info.getLong("phone_number");
        email = user_info.getString("email");

        profileRef = fs.getReference(username + "/profilepicture.jpeg");

        loadProfilePicture();

        addProfilePicture();

        loadCurrentInfo(username, phone_number, email);

        cancelEdition();

        saveEdition();
    }

    private void loadProfilePicture() {

        profileRef.getBytes(MEGA_BYTE).addOnSuccessListener(bytes -> {

            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            ImageView profile_picture = findViewById(R.id.edit_profile_picture);
            profile_picture.setImageBitmap(bitmap);
        });

    }

    private void addProfilePicture() {

        ImageView add = findViewById(R.id.add);
        add.setOnClickListener(v -> {

            Intent open_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(open_camera, CAMERA_REQ_CODE);

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            if(requestCode == CAMERA_REQ_CODE){

                Bitmap img = (Bitmap)data.getExtras().get("data");
                Bitmap resize_img = resizeBitmap(img, 100 ,100);

                ImageView profile_picture = findViewById(R.id.edit_profile_picture);
                profile_picture.setImageBitmap(resize_img);
            }

        }
    }

    public int convertDpToPixels(int dp){
        return (int)(dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public Bitmap resizeBitmap(Bitmap bitmap, int toWidth, int toHeight){
        return Bitmap.createScaledBitmap(bitmap, convertDpToPixels(toWidth), convertDpToPixels(toHeight), false);
    }

    private void loadCurrentInfo(String username, Long phone_number, String email) {
        EditText username_field = findViewById(R.id.edit_username);
        EditText phone_number_field = findViewById(R.id.edit_phone_number);
        EditText email_field = findViewById(R.id.edit_email_address);

        username_field.setText(username);
        phone_number_field.setText(String.valueOf(phone_number));
        email_field.setText(email);
    }

    private void cancelEdition(){

        Button cancel = findViewById(R.id.edit_cancel);
        cancel.setOnClickListener(v -> {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void saveEdition(){

        Button save = findViewById(R.id.edit_save);
        save.setOnClickListener(v -> {

            String current_username, current_email;
            Long current_phone_number;

            EditText username_field = findViewById(R.id.edit_username);
            EditText phone_number_field = findViewById(R.id.edit_phone_number);
            EditText email_field = findViewById(R.id.edit_email_address);

            current_username = String.valueOf(username_field.getText());
            current_phone_number = Long.valueOf(String.valueOf(phone_number_field.getText()));
            current_email = String.valueOf(email_field.getText());

            if(!current_username.equals(username)){

                changeUsername(username, current_username);
                username = current_username;
            }

            if(current_phone_number != phone_number){
                changePhoneNumber(username, current_phone_number);
            }

            if(!current_email.equals(email)){
                changeEmail(username, current_email);
            }

            savePictureInStorage();

            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void savePictureInStorage() {

        ImageView profile_picture = findViewById(R.id.edit_profile_picture);
        Bitmap bitmap = ((BitmapDrawable)profile_picture.getDrawable()).getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = profileRef.putBytes(data);
        uploadTask.addOnCompleteListener((Activity) this, task -> Log.i("MA", "Upload Task Complete"));
    }

    private void changeUsername(String username, String current_username) {
        db.collection("users").document(username).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                Map<String, Object> data = document.getData();

                db.collection("users").document(current_username)
                        .set(data).addOnCompleteListener(task1 -> {
                            if(task.isSuccessful()){
                                db.collection("users").document(username).delete();
                            }
                        });
            }
        });

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", current_username);

        editor.commit();

    }


    private void changePhoneNumber(String username, Long current_phone_number) {

        db.collection("users").document(username).update("phone-number", current_phone_number);

    }

    private void changeEmail(String username, String current_email) {

        db.collection("users").document(username).update("email", current_email);

        String password = sp.getString("password", "");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
            user1.updateEmail(current_email);

        });
    }


}