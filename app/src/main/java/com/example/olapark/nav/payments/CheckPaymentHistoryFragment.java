package com.example.olapark.nav.payments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.olapark.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckPaymentHistoryFragment extends Fragment {

    private View v;

    private SharedPreferences sp;

    private FirebaseFirestore db;

    private RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_check_payment_history, container, false);

        recyclerView = v.findViewById(R.id.recyclerview);

        sp = getActivity().getSharedPreferences("auto-login", MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();

        String username = sp.getString("username" , "");

        addPayments(username);

        return v;
    }



    private void addPayments(String username) {

        db.collection("users")
                .document(username)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){

                        DocumentSnapshot document = task.getResult();

                        List<Payment> p = new ArrayList<>();

                        Map<String, String> payments = (Map<String, String>) document.get("payments");
                        for(Map.Entry<String, String> payment: payments.entrySet()){

                            String values = payment.getValue();

                            String[] aux = values.split("%");

                            p.add(new Payment(payment.getKey(), aux[0], aux[1], aux[2]));

                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            p.sort(new Comparator<Payment>() {
                                @Override
                                public int compare(Payment p1, Payment p2) {
                                    return Integer.compare(Integer.parseInt(p1.getNumber()), Integer.parseInt(p2.getNumber()));
                                }
                            });
                        }

                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.setAdapter(new MyAdapter(getActivity(), p));

                    }
                });





    }


}