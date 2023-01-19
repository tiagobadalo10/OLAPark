package com.example.olapark.nav.payments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olapark.R;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    Context context;
    List<Payment> payments;

    public MyAdapter(Context context, List<Payment> payments){
        this.context = context;
        this.payments = payments;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.payment_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.number.setText(payments.get(position).getNumber());
        holder.date.setText(payments.get(position).getDate());
        holder.name.setText(payments.get(position).getPark());
        holder.value.setText(payments.get(position).getValue());
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }
}
