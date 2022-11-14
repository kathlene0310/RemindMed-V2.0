package com.example.capstone1.dependent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone1.R;
import com.example.capstone1.medication_info;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class myHomeAdapter2 extends RecyclerView.Adapter<myHomeAdapter2.MyViewHolder> {
    Context context;
    ArrayList<medication_info> userArrayList;
    public myHomeAdapter2(Context context, ArrayList<medication_info> userArrayList)
    {
        this.context = context;
        this.userArrayList = userArrayList;
    }
    @NonNull
    @Override
    public myHomeAdapter2.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.alarms_set, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myHomeAdapter2.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        medication_info Medication = userArrayList.get(position);
        holder.Medication.setText(Medication.getMedication());
        holder.Time.setText(Medication.getTime());
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, editDeleteMedicationsDependent.class);
                DateFormat dateFormat = new SimpleDateFormat("M/dd/yyyy");
                medication_info medication_info = userArrayList.get(position);
                String strDate = dateFormat.format(medication_info.getStartDate());
                String strEndDate = dateFormat.format(medication_info.getEndDate());
                intent.putExtra("description", medication_info.getMedication());
                intent.putExtra("pill", medication_info.getInventoryMeds());
                intent.putExtra("startdate", strDate);
                intent.putExtra("time", medication_info.getTime());
                intent.putExtra("enddate", strEndDate);
                intent.putExtra("dosage", medication_info.getDosage());
                intent.putExtra("FrequencyTitle", medication_info.getFrequencyName());
                intent.putExtra("MedicineTitle", medication_info.getMedicineTypeName());
                intent.putExtra("medication_info", medication_info);
                intent.putExtra("AlarmID", medication_info.getAlarmID());
                intent.putExtra("NotifyChoice", medication_info.getNotifyChoice());
                intent.putExtra("Hour", medication_info.getHour());
                intent.putExtra("Minute", medication_info.getMinute());
                intent.putExtra("USER_CHOSEN", medication_info.getUserId());


                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Medication, Time;
        Button edit, delete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Medication = itemView.findViewById(R.id.medNameHome);
            Time = itemView.findViewById(R.id.medTimeHome);
            edit = itemView.findViewById(R.id.buttonEdit);
        }


    }

}
