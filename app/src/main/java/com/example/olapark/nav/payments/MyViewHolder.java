package com.example.olapark.nav.payments;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olapark.R;

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView number, date, name, value;


    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        number = itemView.findViewById(R.id.number);
        date = itemView.findViewById(R.id.date);
        name = itemView.findViewById(R.id.name);
        value = itemView.findViewById(R.id.value);
    }
}
