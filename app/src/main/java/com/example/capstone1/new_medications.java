package com.example.capstone1;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class new_medications extends AppCompatActivity  implements TimePickerDialog.OnTimeSetListener {
    EditText medication, dosage, inventory;
    Button buttonsavemedication;
    FirebaseAuth rootAuthen;
    FirebaseFirestore fstore;
    String userId;
    Spinner spinnertypeunit, spinnerfrequencymedication;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    FloatingActionButton timeSetBtn;

    //CollectionReference reference = fstore.collection("Users");

    Button timeButtonmedtst, dateformat;
    int hour, minute;
    int year, month, day;


    private static final String TAG = "new_medications";

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    //added spinner and timePicker

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_medications);
        createNotificationChannel();
        medication = findViewById(R.id.medicine_Box);
        dosage = findViewById(R.id.DosageBox);
        inventory = findViewById(R.id.inventoryBox);
        spinnertypeunit = findViewById(R.id.type_spinner_one);
        spinnerfrequencymedication = findViewById(R.id.frequency_spinner_ten);
        buttonsavemedication = findViewById(R.id.save_medication_button);
        rootAuthen = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        timeButtonmedtst = findViewById(R.id.timeButtonmed);

        timeButtonmedtst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(new_medications.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                hour = i;
                                minute = i1;
                                String time = hour + ":" + minute;
                                SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm"
                                );
                                try {
                                    Date date = f24Hours.parse(time);
                                    SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa"
                                    );
                                    timeButtonmedtst.setText(f12Hours.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(hour, minute);
                timePickerDialog.show();
            }
        });


        userId = rootAuthen.getCurrentUser().getUid();

        //calendar but di pa oks ayaw mapalitan ng naka show
        dateformat = findViewById(R.id.startButton_date);
        /*timeSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timepicker = new TimePickerFragment();
                timepicker.show(getSupportFragmentManager(), "time picker");
            }
        });*/


        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(new_medications.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.frequency));
        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerfrequencymedication.setAdapter(myAdapter2);

        ArrayAdapter<String> myAdapter_two = new ArrayAdapter<String>(new_medications.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.type));
        myAdapter_two.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnertypeunit.setAdapter(myAdapter_two);


        buttonsavemedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Medication = medication.getText().toString().trim();
                String Dosage = dosage.getText().toString().trim();
                String Inventory = inventory.getText().toString().trim();

                Map<String, Object> user = new HashMap<>();
                user.put("Medication", Medication);
                user.put("Dosage", Dosage);
                user.put("InventoryMeds", Inventory);


                fstore.collection("users").document(userId).collection("New Medications")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(new_medications.this, "New Medication added", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onSuccess: failed");
                            }
                        });
                setAlarm();
            }
        });


    }


    public void Medication_To_Home(View view) {
        Intent intent = new Intent(new_medications.this, home_page.class);
        startActivity(intent);
    }

    public void Medication_To_OCR(View view) {
        Intent intent = new Intent(new_medications.this, optical_character_recognition.class);
        startActivity(intent);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //TextView timeTV = (TextView) findViewById(R.id.timeTV);
        //timeTV.setText(hourOfDay + ":" + minute);
    }
    private void setAlarm() {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, alarmreceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0 , intent, 0 );
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(this, "Alarm Set Succesfully", Toast.LENGTH_SHORT).show();

    }

    /*public void popTimePicker (View view){
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                hour = i;
                minute = i1;
                timeButtonmedtst.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }
        };

        int style = AlertDialog.THEME_HOLO_DARK;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, style, onTimeSetListener, hour, minute, true);
        timePickerDialog.setTitle("Set Time");
        timePickerDialog.show();
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }*/

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "MyApp";
            String description = "My app manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("MyApp", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }
}
// }
//}