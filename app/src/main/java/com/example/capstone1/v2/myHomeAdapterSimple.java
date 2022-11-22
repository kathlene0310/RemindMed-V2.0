package com.example.capstone1.v2;

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
import com.example.capstone1.edit_delete_medications;
import com.example.capstone1.medication_info;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class myHomeAdapterSimple extends RecyclerView.Adapter<myHomeAdapterSimple.MyViewHolder> {
    Context context;
    ArrayList<medication_info> userArrayList;
    public myHomeAdapterSimple(Context context, ArrayList<medication_info> userArrayList)
    {
        this.context = context;
        this.userArrayList = userArrayList;
    }
    @NonNull
    @Override
    public myHomeAdapterSimple.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.v2_simple_mode_view_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myHomeAdapterSimple.MyViewHolder holder, int position) {
        medication_info Medication = userArrayList.get(position);
        holder.Medication.setText(Medication.getMedication());
        holder.Time.setText(Medication.getTime());
        holder.Dosage.setText(Medication.getDosage().toString());
        holder.Frequency.setText(Medication.getFrequencyName());
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, edit_delete_medications.class);
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


                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Medication, Time, Dosage, Frequency;
        Button edit, delete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Medication = itemView.findViewById(R.id.medNameHome);
            Dosage = itemView.findViewById(R.id.medTimeHome);
            edit = itemView.findViewById(R.id.buttonEdit);
            Time = itemView.findViewById(R.id.medTime);
            Frequency = itemView.findViewById(R.id.medFrequency);

        }


    }

}
