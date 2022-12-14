package com.example.capstone1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HMAdapterToday extends RecyclerView.Adapter<HMAdapterToday.MyViewHolder> {
    Context context;
    String dateToday, dateselectedString;
    ArrayList<measurement_info_today> userArrayList;
    Date c = Calendar.getInstance().getTime();


    public HMAdapterToday(Context context, ArrayList<measurement_info_today> userArrayList)
    {
        this.context =context;
        this.userArrayList = userArrayList;
    }
    @NonNull
    @Override
    public HMAdapterToday.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.items_today_measurements, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HMAdapterToday.MyViewHolder holder, int position) {
        measurement_info_today measurement_info_today = userArrayList.get(position);
        DateFormat dateFormat = new SimpleDateFormat("M/dd/yyyy");
        String strEnd = dateFormat.format(measurement_info_today.EndDate);
        String strDate = dateFormat.format(measurement_info_today.StartDate);
        holder.HMName.setText(measurement_info_today.HMName);
        holder.Time.setText(measurement_info_today.Time);

        SimpleDateFormat df = new SimpleDateFormat("M/dd/yyyy", Locale.getDefault());
        dateToday = df.format(c);
        dateselectedString = df.format(today_page_recycler.dateSelected);
        if(dateselectedString.equals(dateToday))
            holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    measurement_info_today measurement_info_today = userArrayList.get(position);
                    Intent intent;
                    if(measurement_info_today.HMName.equals("Bloodpressure"))
                    {
                        intent = new Intent(context, set_now_blood_pressure.class);
                        intent.putExtra("Time", measurement_info_today.Time);
                        intent.putExtra("Date", strDate);
                        intent.putExtra("EndDate", strEnd);
                        intent.putExtra("fromToday", 1);
                        intent.putExtra("Frequency", measurement_info_today.Frequency);
                        intent.putExtra("Hour", measurement_info_today.Hour);
                        intent.putExtra("Minute", measurement_info_today.Minute);
                        intent.putExtra("measuremy_info_today", measurement_info_today);
                        intent.putExtra("ID", measurement_info_today.idCode);


                        context.startActivity(intent);

                    }
                    else if(measurement_info_today.HMName.equals("Cholesterol"))
                    {
                        intent = new Intent(context, set_now_cholesterol.class);
                        intent.putExtra("Time", measurement_info_today.Time);
                        intent.putExtra("Date", strDate);
                        intent.putExtra("EndDate", strEnd);
                        intent.putExtra("fromToday", 1);
                        intent.putExtra("Frequency", measurement_info_today.Frequency);
                        intent.putExtra("Hour", measurement_info_today.Hour);
                        intent.putExtra("Minute", measurement_info_today.Minute);
                        intent.putExtra("measuremy_info_today", measurement_info_today);;
                        intent.putExtra("ID", measurement_info_today.idCode);


                        context.startActivity(intent);
                    }
                    else if(measurement_info_today.HMName.equals("Bloodsugar"))
                    {
                        intent = new Intent(context, set_now_blood_sugar.class);
                        intent.putExtra("Time", measurement_info_today.Time);
                        intent.putExtra("Date", strDate);
                        intent.putExtra("EndDate", strEnd);
                        intent.putExtra("fromToday", 1);
                        intent.putExtra("Frequency", measurement_info_today.Frequency);
                        intent.putExtra("measuremy_info_today", measurement_info_today);
                        intent.putExtra("Hour", measurement_info_today.Hour);
                        intent.putExtra("Minute", measurement_info_today.Minute);
                        intent.putExtra("ID", measurement_info_today.idCode);



                        context.startActivity(intent);
                    }
                    else if(measurement_info_today.HMName.equals("Temperature"))
                    {
                        intent = new Intent(context, set_now_temperature.class);
                        intent.putExtra("Time", measurement_info_today.Time);
                        intent.putExtra("Date", strDate);
                        intent.putExtra("EndDate", strEnd);
                        intent.putExtra("fromToday", 1);
                        intent.putExtra("Frequency", measurement_info_today.Frequency);
                        intent.putExtra("measuremy_info_today", measurement_info_today);
                        intent.putExtra("Hour", measurement_info_today.Hour);
                        intent.putExtra("Minute", measurement_info_today.Minute);
                        intent.putExtra("ID", measurement_info_today.idCode);


                        context.startActivity(intent);
                    }
                    else if(measurement_info_today.HMName.equals("Sleep"))
                    {
                        intent = new Intent(context, set_now_hours_of_sleep.class);
                        intent.putExtra("Time", measurement_info_today.Time);
                        intent.putExtra("Date", strDate);
                        intent.putExtra("EndDate", strEnd);
                        intent.putExtra("fromToday", 1);
                        intent.putExtra("Frequency", measurement_info_today.Frequency);
                        intent.putExtra("measuremy_info_today", measurement_info_today);
                        intent.putExtra("Hour", measurement_info_today.Hour);
                        intent.putExtra("Minute", measurement_info_today.Minute);
                        intent.putExtra("ID", measurement_info_today.idCode);


                        context.startActivity(intent);
                    }
                    else if(measurement_info_today.HMName.equals("Pulserate"))
                    {
                        intent = new Intent(context, set_now_pulse_rate.class);
                        intent.putExtra("Time", measurement_info_today.Time);
                        intent.putExtra("Date", strDate);
                        intent.putExtra("EndDate", strEnd);
                        intent.putExtra("fromToday", 1);
                        intent.putExtra("Frequency", measurement_info_today.Frequency);
                        intent.putExtra("measuremy_info_today", measurement_info_today);
                        intent.putExtra("Hour", measurement_info_today.Hour);
                        intent.putExtra("Minute", measurement_info_today.Minute);
                        intent.putExtra("ID", measurement_info_today.idCode);


                        context.startActivity(intent);
                    }

                }
            });

    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView HMName, Time;
        ConstraintLayout constraintLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            HMName = itemView.findViewById(R.id.todayHM);
            Time  = itemView.findViewById(R.id.todayTimeHM);
            constraintLayout = itemView.findViewById(R.id.measurementLayout);
        }
    }
}
