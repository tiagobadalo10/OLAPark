package com.example.olapark;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

public class ReportActivity extends Activity {

    private final int CAMERA_REQ_CODE = 100;
    private ImageView imgCamera;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Fill spinner with options
        Spinner spinner = findViewById(R.id.report_options);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.report_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        findViewById(R.id.report_picture);

        ImageButton camera = findViewById(R.id.report_camera);
        // If camera button is clicked
        camera.setOnClickListener(v -> {
            Intent open_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(open_camera, CAMERA_REQ_CODE);
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            if(requestCode == CAMERA_REQ_CODE){

               Bitmap img = (Bitmap) data.getExtras().get("data");
               imgCamera.setImageBitmap(img);
            }
        }
    }


}