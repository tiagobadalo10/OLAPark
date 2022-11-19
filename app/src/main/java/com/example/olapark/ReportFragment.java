package com.example.olapark;


import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import com.example.olapark.databinding.FragmentReportBinding;


public class ReportFragment extends Fragment {

    private FragmentReportBinding binding;
    private final int CAMERA_REQ_CODE = 100;
    private ImageView imgCamera;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentReportBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        // Fill spinner with options
        Spinner spinner = view.findViewById(R.id.report_options);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.report_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        imgCamera = view.findViewById(R.id.report_picture);

        ImageButton camera = view.findViewById(R.id.report_camera);
        // If camera button is clicked
        camera.setOnClickListener(v -> {
            Intent open_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(open_camera, CAMERA_REQ_CODE);
        });
        return view;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}