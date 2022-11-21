package com.example.capstone1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class pdfAdapterMedication extends RecyclerView.Adapter<pdfAdapterMedication.MyViewHolder> {
    Context context;
    ArrayList<medication_history_info> userArrayList;

    public pdfAdapterMedication(Context context, ArrayList<medication_history_info> userArrayList)
    {
        this.context =context;
        this.userArrayList = userArrayList;
    }
    @NonNull
    @Override
    public pdfAdapterMedication.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.table_layout_history_medication, parent, false);
        return new pdfAdapterMedication.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull pdfAdapterMedication.MyViewHolder holder, int position) {
        medication_history_info measurment_info = userArrayList.get(position);
        holder.Record.setText(measurment_info.Medication);
        holder.Time.setText(measurment_info.Time);
        holder.Date.setText(measurment_info.StartDate);

    }

    @Override
    public int getItemCount() {
        return  userArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Record, Time,  Date;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Record = itemView.findViewById(R.id.measurementhistoryPDF);
            Time = itemView.findViewById(R.id.timehisoryPDF);
            Date = itemView.findViewById(R.id.datehistoryPDF);
        }
    }
}
