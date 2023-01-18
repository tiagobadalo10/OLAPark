package com.example.olapark.nav.parks;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.olapark.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.protobuf.Enum;

import org.joda.time.DateTime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OccupationParkDialog extends DialogFragment {

    private View view;
    private Park park;
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;

    private final String url = "https://roads.googleapis.com/v1/snapToRoads?interpolate=true&path=";
    private final String key = "AIzaSyBx64LbDqZGT7otVA_QFu_QHJAHeA7A8kQ";

    public static OccupationParkDialog newInstance(String title) {
        OccupationParkDialog yourDialogFragment = new OccupationParkDialog();

        Bundle args = new Bundle();
        args.putString("title", title);
        yourDialogFragment.setArguments(args);

        return yourDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.layout_occupation_park_dialog, container, false);

        db = FirebaseFirestore.getInstance();
        submit();
        cancel();

        return view;
    }

    private void submit() {

        Button report = view.findViewById(R.id.submit_button);
        report.setOnClickListener(v -> {

            RadioGroup myRadioGroup = view.findViewById(R.id.occupation_group);
            int selectedId = myRadioGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = (RadioButton) view.findViewById(selectedId);

            String selectedText = selectedRadioButton.getText().toString();
            Occupation occupation = Occupation.valueOf(selectedText);

            updateDocument(park.getName(), occupation);

            dismiss();
        });
    }

    public void updateDocument(String documentId, Occupation occupationEnum) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("parks").document(documentId);

        listenerRegistration = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("MyActivity", "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Map<String, Object> data = snapshot.getData();

                    Map occupation = (Map) data.get("occupation");

                    DateTime now = DateTime.now();
                    long occupationLevel = occupationEnumToInt(occupationEnum);

                    Map<String, Object> update = filterOccupation(occupation);

                    update.put(now.toString(), occupationLevel);

                    docRef.update("occupation", update).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("OccupationParkDialog", "Document update successful!");
                            } else {
                                Log.w("OccupationParkDialog", "Error updating document", task.getException());
                            }
                        }
                    });
                }
            }
        });
    }

    public Map filterOccupation(Map occupationMap) {

        Map<String, Object> update = new HashMap<>();
        Iterator<Map.Entry<String, Long>> itr = occupationMap.entrySet().iterator();

        while(itr.hasNext()) {
            Map.Entry<String, Long> entry = itr.next();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                LocalDateTime dateTime = LocalDateTime.parse(entry.getKey(), formatter);
                if (DateTimeUtils.isWithinLast30Minutes(dateTime)) {
                    update.put(entry.getKey(), entry.getValue());
                }
            }
        }

        return update;
    }


    private int occupationEnumToInt(Occupation occupation) {
        if (occupation.equals(Occupation.LOW)) {
            return 1;
        }
        if (occupation.equals(Occupation.MEDIUM)) {
            return 2;
        }
        if (occupation.equals(Occupation.HIGH)) {
            return 3;
        }
        return  0;
    }

    private void cancel() {

        Button report = view.findViewById(R.id.cancel_button);
        report.setOnClickListener(v -> {
            dismiss();
        });
    }

    public void setPark(Park park) {
        if (park == null) {
            dismiss();
        }
        this.park = park;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }


}
