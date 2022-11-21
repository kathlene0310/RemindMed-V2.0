package com.example.capstone1.simple;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.capstone1.R;
import com.example.capstone1.TimePickerFragment;
import com.example.capstone1.alarmreceiver;
import com.example.capstone1.home_page;
import com.example.capstone1.new_medications;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class add_medication extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    EditText  dosage ;
    Button buttonsavemedication, notfinInventory;
    FirebaseAuth rootAuthen;
    FirebaseFirestore fstore;
    final int start = 1;
    final int end = 2;
    String userId, startdate, enddate;
    Spinner spinnertypeunit, spinnerfrequencymedication;
    Calendar calendar = Calendar.getInstance();;
    private DatePickerDialog datePickerDialog, datePickerDialog2;
    private Button dateButton, endDateButton;
    Calendar c;
    ImageView helpdosage, helptype, helpinventory;
    Calendar myAlarmDate = Calendar.getInstance();
    static final SimpleDateFormat format = new SimpleDateFormat("M/dd/yyyy");
    Button timeButtonmedtst;
    int alarmYear, alarmMonth, alarmDay,alarmHour,alarmMin, choice, typechoice, frequencychoide, alarmID;
    String dateToday = String.valueOf(android.text.format.DateFormat.format("M/dd/yyyy", new java.util.Date()));
    static EditText medication;
    static EditText inventory;
    private static final String TAG = "new_medications";
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_simple_mode_add_medication);

        rootAuthen = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        medication = findViewById(R.id.frequency_set_later_bs);
        dosage = findViewById(R.id.frequency_set_later_bs2);
        inventory = findViewById(R.id.frequency_set_later_bs6);
        timeButtonmedtst = findViewById(R.id.frequency_set_later_bs5);
        buttonsavemedication = findViewById(R.id.resetbtn2);
        spinnertypeunit = findViewById(R.id.frequency_set_later_bs4);
        spinnerfrequencymedication = findViewById(R.id.frequency_set_later_bs7);
        dateButton = findViewById(R.id.startButton_date4);
        endDateButton = findViewById(R.id.endButton_one4);
        userId = rootAuthen.getCurrentUser().getUid();



        ArrayAdapter<String> adapterFrequency = new ArrayAdapter<String>(add_medication.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array
                .frequency));
        adapterFrequency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerfrequencymedication.setAdapter(adapterFrequency);

        ArrayAdapter<String> adapterUnit = new ArrayAdapter<String>(add_medication.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.type));

        adapterUnit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnertypeunit.setAdapter(adapterUnit);

        spinnertypeunit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typechoice = position;
                if (position == 4)
                {
                    dosage.setText(String.valueOf(15));
                    dosage.setFocusable(false);
                    dosage.setEnabled(false);
                }
                else
                {
                    dosage.setFocusableInTouchMode(true);
                    dosage.setEnabled(true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerfrequencymedication.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                frequencychoide = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        timeButtonmedtst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = 1;
                initDatePicker();
                openDatePicker();
            }
        });
        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = 2;
                initDatePicker();
                openDatePicker();
            }
        });


        buttonsavemedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(add_medication.this, home_page.class);
                    String Medication = medication.getText().toString().trim();
                    String Dosage = dosage.getText().toString();
                    String frequencyName = spinnerfrequencymedication.getSelectedItem().toString();
                    String medicationTypeName = spinnertypeunit.getSelectedItem().toString();
                    String Inventory = inventory.getText().toString().trim();
                    String Time = timeButtonmedtst.getText().toString().trim();
                    String StartDate = dateButton.getText().toString().trim();
                    String EndDate = endDateButton.getText().toString().trim();
                    alarmID = new Random().nextInt(1000000);
                    Map<String, Object> user = new HashMap<>();
                    user.put("Medication", Medication);
                    user.put("Dosage", Dosage);
                    user.put("InventoryMeds", Inventory);
                    user.put("Time", Time);
                    user.put("StartDate", getDateFromString(StartDate));
                    user.put("EndDate", getDateFromString(EndDate));
                    user.put("MedicineType", typechoice);
                    user.put("MedicineTypeName", medicationTypeName);
                    user.put("Frequency", frequencychoide);
                    user.put("FrequencyName", frequencyName);
                    user.put("PillStatic", Integer.parseInt(Inventory));
                    user.put("NotifyChoice", "YES");
                    user.put("Hour", alarmHour);
                    user.put("Minute", alarmMin);
                    user.put("AlarmID", alarmID);



                    Log.d("class", "start " + alarmHour);


                    if (TextUtils.isEmpty(Medication)) {
                        medication.setError("This field is required");
                        return;
                    }
                    if (TextUtils.isEmpty(Dosage)) {
                        dosage.setError("This field is required");
                        return;
                    }
                    if (TextUtils.isEmpty(Inventory)) {
                        inventory.setError("This field is required");
                        return;
                    }
                    if (Integer.parseInt(dosage.getText().toString()) > Integer.parseInt(inventory.getText().toString())) {
                        dosage.setError("Dosage should be less than the Inventory");
                        return;

                    }
                    if (Integer.parseInt(dosage.getText().toString()) < 0) {
                        dosage.setError("Dosage should not be a negative number");
                        return;

                    }
                    if (Integer.parseInt(inventory.getText().toString()) < 0) {
                        inventory.setError("Dosage should not be a negative number");
                        return;

                    }
                    if (spinnertypeunit.getSelectedItemPosition() == 0) {
                        Toast.makeText(getApplicationContext(), "Please select type", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (spinnerfrequencymedication.getSelectedItemPosition() == 0) {
                        Toast.makeText(getApplicationContext(), "Please select frequency", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (timeButtonmedtst.getText().toString().equals("Set Time")) {
                        Toast.makeText(getApplicationContext(), "Please select Time", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (dateButton.getText().toString().equals("Start")) {
                        Toast.makeText(getApplicationContext(), "Please select Start Date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (endDateButton.getText().toString().equals("End")) {
                        Toast.makeText(getApplicationContext(), "Please select End Date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (getDateFromString(startdate).after(getDateFromString(enddate))) {
                        Toast.makeText(getApplicationContext(), "Start Date should be before End Date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    setDate(myAlarmDate);
                    if (myAlarmDate.getTimeInMillis() < System.currentTimeMillis()) {
                        Toast.makeText(add_medication.this, "Set the time and date to the future", Toast.LENGTH_LONG).show();
                        return;

                    }
                    else {
                        startAlarm(myAlarmDate);


                    }


                    fstore.collection("users").document(userId).collection("New Medications")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(add_medication.this, "New Medication added", Toast.LENGTH_SHORT).show();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onSuccess: failed");
                                }
                            });
                    Log.d("class", "Hour and minute" + dateToday);
                    startActivity(intent);



                } catch (Exception e) {
                    Toast.makeText(add_medication.this, "Fill in all the fields", Toast.LENGTH_SHORT).show();

                }

            }
        });


    }

    public void Add_Medication_To_Home(View view) {
        Intent intent = new Intent(this, shome_page.class);
        startActivity(intent);
    }

    public void Add_Medication_To_Profile(View view) {
        Intent intent = new Intent(this, profile.class);
        startActivity(intent);
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MyApp";
            String description = "My app manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("MyApp", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setDate(Calendar c)
    {
        myAlarmDate.setTimeInMillis(System.currentTimeMillis());
        myAlarmDate.set(alarmYear, alarmMonth, alarmDay, alarmHour, alarmMin);
    }


    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month+=1;
                if (choice == start)
                {

                    startdate = makeDateString(day, month, year);
                    alarmYear = year ;
                    alarmMonth = month - 1;
                    alarmDay = day;
                    dateButton.setText(startdate);
                }
                else if (choice == end)
                {
                    enddate = makeDateString(day, month, year);
                    endDateButton.setText(enddate);

                }
            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
    }

    private String makeDateString(int day, int month, int year)
    {


        if (day<10)
        {
            return month + "/" +"0" +day + "/" + year;
        }
        else
        {
            return month + "/"  +day + "/" + year;
        }

    }

    public void openDatePicker() {
        datePickerDialog.show();
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        alarmHour = hourOfDay;
        alarmMin = minute;
        updateTimeText(c);
    }

    private void updateTimeText(Calendar c)
    {
        String timeText = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
        timeButtonmedtst.setText(timeText);
    }

    private void startAlarm(Calendar c)
    {
        myAlarmDate.setTimeInMillis(System.currentTimeMillis());
        myAlarmDate.set(alarmYear, alarmMonth, alarmDay, alarmHour, alarmMin);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, alarmreceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmID, i, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, myAlarmDate.getTimeInMillis(), pendingIntent);

    }

    public Date getDateFromString(String dateToSave) {
        try {
            Date date = format.parse(dateToSave);
            return date ;
        } catch (ParseException e){
            return null ;
        }
    }
}
