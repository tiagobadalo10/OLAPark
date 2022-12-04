package com.example.olapark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.olapark.api.WebRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

public class Profile extends AppCompatActivity implements IPickResult {

    private String username;
    private FirebaseFirestore db;
    private String choose, cancel, camera, gallery, loadingImage;
    private SharedPreferences sharedPreferences;
    private String SHARED_PREF_NAME ="user_pref";

    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView phoneNumberTextView;
    private ImageButton addCar;
    private String compressed;
    private ProgressBar progressBar;
    private DateFormat df;
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences sh = getSharedPreferences("auto-login", MODE_PRIVATE);

        username = sh.getString("username", null);
        db = FirebaseFirestore.getInstance();

        usernameTextView = (TextView) findViewById(R.id.username_txt);
        emailTextView = (TextView) findViewById(R.id.email_txt);
        phoneNumberTextView = (TextView) findViewById(R.id.phone_number_txt);
        addCar = (ImageButton) findViewById(R.id.report_car);
        choose = getString(R.string.modal_add_card_choose);
        cancel = getString(R.string.modal_add_card_cancel);
        camera = getString(R.string.modal_add_card_camera);
        gallery = getString(R.string.modal_add_card_gallery);
        loadingImage = getString(R.string.modal_add_card_loading_image);
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        progressBar = findViewById(R.id.progressBarCar);
        date = new Date();
        df = new SimpleDateFormat("MM/dd/");
        df.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

        addCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImageDialog.build(setup).show(Profile.this);
            }
        });

        setProfileInfo();
        createTableCars();
    }

    PickSetup setup = new PickSetup()
            .setTitle(choose)
            .setCancelText(cancel)
            .setFlip(true)
            .setMaxSize(50)
            .setWidth(50)
            .setHeight(50)
            .setProgressText(loadingImage)
            .setPickTypes(EPickType.GALLERY, EPickType.CAMERA)
            .setCameraButtonText(camera)
            .setGalleryButtonText(gallery)
            .setIconGravity(Gravity.TOP)
            .setButtonOrientation(LinearLayout.HORIZONTAL)
            .setSystemDialog(false)
            .setGalleryIcon(R.drawable.photo)
            .setCameraIcon(R.drawable.cam);

    private void createRowTableCars(String plate, String type) {
        TableRow row = new TableRow(Profile.this);
        TableRow.LayoutParams paramsExample = new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT,0.4f);

        TextView plateView = new TextView(Profile.this);
        plateView.setText(plate);
        plateView.setTextSize(16);
        plateView.setLayoutParams(paramsExample);

        TextView carView = new TextView(Profile.this);
        carView.setText(type);
        carView.setTextSize(16);
        carView.setLayoutParams(paramsExample);

        row.addView(plateView);
        row.addView(carView);

        TableLayout table = (TableLayout) findViewById(R.id.tableCards);
        table.addView(row);
    }

    private void createTableCars() {
        db.collection("users").document(username).collection("cars").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        createRowTableCars(document.getString("plate"), document.getString("type"));
                    }
                }
            }
        });
    }

    private void setProfileInfo() {
        db.collection("users").document(username).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String email = documentSnapshot.getString("email");
                            Long phoneNumber = documentSnapshot.getLong("phone-number");

                            //set values
                            usernameTextView.setText(username);
                            emailTextView.setText(email);
                            phoneNumberTextView.setText(String.valueOf(phoneNumber));
                        }
                    }
                });

    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            RequestParams params = new RequestParams();
            String file = r.getPath();
            String countryCode = sharedPreferences.getString("RegionCode", "");
            String baseUrl = sharedPreferences.getString("BaseUrl","https://api.platerecognizer.com/v1/plate-reader/");

            try {
                params.put("upload", new File(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            params.put("regions", countryCode);
            WebRequest.client.addHeader("Authorization","Token 7febb00de215fa3cf5c0b9563b70cb8ad41dee1a");
            WebRequest.post(this, baseUrl, params, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    progressBar.setVisibility(View.VISIBLE);
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);

                    try {
                        JSONArray results = response.getJSONArray("results");
                        if (results.length() > 0) {
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject tabObj = results.getJSONObject(i);
                                Map<String, Object> car = new HashMap<>();
                                String plate = tabObj.getString("plate");
                                String type = tabObj.getJSONObject("vehicle").getString("type");

                                car.put("plate", plate);
                                car.put("type", type);

                                db.collection("users").document(username).collection("cars").add(car);
                                createRowTableCars(plate, type);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Profile.this, "Some errors occurs while getting data from the car, try again!", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(Profile.this, "Error uploading this image, try again!", Toast.LENGTH_LONG).show();
        }
    }
}