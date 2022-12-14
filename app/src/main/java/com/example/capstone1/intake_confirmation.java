package com.example.capstone1;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.capstone1.v2.SharedPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class intake_confirmation extends AppCompatActivity {
    Float speed, pitch;
    String voice;

    TextView medName, medAmount, dateTakentxt;
    String title, amount, time, date, enddate, dosage, userId, text, notify;
    Date myDate;
    int alarmYear, alarmMonth, alarmDay,alarmHour,alarmMin, id ,freq, alarmID;
    Calendar myAlarmDate = Calendar.getInstance();
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
    TextToSpeech textToSpeech;
    Button confirm, skip;
    FloatingActionButton tts;
    FirebaseFirestore db;
    FirebaseAuth rootAuthen;
    String frequency, medtype;
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private medication_info medication_info;
    private static final String TAG = "intake_confirmation";
    SharedPref sf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intake_confirmation);
        createNotificationChannel();
        medication_info = (medication_info) getIntent().getSerializableExtra("medication_info");
        db = FirebaseFirestore.getInstance();
        medName = findViewById(R.id.medNameConfirmTv);
        medAmount = findViewById(R.id.medInventoryTV);
        confirm = findViewById(R.id.confirm_btn);
        skip = findViewById(R.id.skip_btn);
        dateTakentxt = findViewById(R.id.dateTaken);
        rootAuthen = FirebaseAuth.getInstance();
        userId = rootAuthen.getCurrentUser().getUid();
        tts = findViewById(R.id.ttsButton);

        sf = new SharedPref(getApplicationContext());

        voice = sf.getVoice();
        speed = sf.getSpeed();
        pitch = sf.getPitch();

        getData();
        setData();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel
                    ("abnormalbp", "abnormalbp", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS)
                {
                    int result = textToSpeech.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                    {
                        Log.e("TTS", "Language not supported");
                    }
                    else
                    {
                        tts.setEnabled(true);
                    }
                }
                else
                {
                    Log.e("TTS", "Iniitialization failed");
                }
            }
        });

        tts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = "The medication you are taking is " + title;
                speak();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
                if (Integer.parseInt(dosage)>Integer.parseInt(amount))
                {
                    Toast.makeText(intake_confirmation.this, "Inventory is not enough please refill first", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    decrementMedication();
                    if (freq == 1)
                    {
                        moveStartDate();
                        if(!date.equals(enddate))
                        {
                            startAlarm(myAlarmDate);
                        }


                    }
                    else if (freq == 2)
                    {
                        moveStartDateWeek();
                        if(!date.equals(enddate))
                        {
                            startAlarm(myAlarmDate);
                        }


                    }
                    saveToHistory();
                    if(Integer.parseInt(medication_info.getInventoryMeds())  <= medication_info.getPillStatic()/2 &&  notify.equals("YES"))
                    {
                        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder)
                                new NotificationCompat.Builder(intake_confirmation.this, "abnormalbp");
                        mBuilder.setSmallIcon(R.drawable.ic_launcher_background);
                        mBuilder.setContentTitle("Inventory Halfway");
                        mBuilder.setContentText(medication_info.getMedication() + " is halfway with its inventory!");
                        mBuilder.setAutoCancel(true);

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(intake_confirmation.this);
                        notificationManager.notify(7, mBuilder.build());

                        AlertDialog.Builder aBuilder = new AlertDialog.Builder(intake_confirmation.this);
                        aBuilder.setCancelable(true);
                        aBuilder.setTitle("Medication Alert");
                        aBuilder.setMessage("You have gone halfway with your medication refill it soon before you run out");

                        aBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                startActivity(new Intent(intake_confirmation.this, today_page_recycler.class));

                            }
                        });

                        aBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(intake_confirmation.this, today_page_recycler.class));
                            }
                        });

                        aBuilder.show();
                    }
                    else
                    {
                        startActivity(new Intent(intake_confirmation.this, today_page_recycler.class));

                    }
                }


            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (freq == 1)
                {
                    moveStartDate();

                    if(!date.equals(enddate))
                    {
                        startAlarm(myAlarmDate);
                    }


                }
                else if (freq == 2)
                {
                    moveStartDateWeek();
                    if(!date.equals(enddate))
                    {
                        startAlarm(myAlarmDate);
                    }


                }
                startActivity(new Intent(intake_confirmation.this, today_page_recycler.class));

            }
        });

    }

    private void getData() {
        if (getIntent().hasExtra("description") && getIntent().hasExtra("pill")) {
            title = getIntent().getStringExtra("description");
            amount = getIntent().getStringExtra("pill");
            time = getIntent().getStringExtra("time");
            date = getIntent().getStringExtra("startdate");
            enddate = getIntent().getStringExtra("enddate");
            dosage = getIntent().getStringExtra("Dosage");
            freq = getIntent().getIntExtra("frequency", 0);
            alarmHour = getIntent().getIntExtra("Hour", 0);
            alarmMin =getIntent().getIntExtra("Minute", 0);
            alarmID = getIntent().getIntExtra("AlarnID",0 );
            notify = getIntent().getStringExtra("NotifyChoce");

        } else {
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }
    }

    private void setData() {
        medName.setText(title);
        medAmount.setText(amount);
        dateTakentxt.setText(time);

    }

    private void decrementMedication() {
        getData();
        int inv = Integer.parseInt(amount);
        int doseInt = Integer.parseInt(dosage);
        inv -= doseInt;

        amount = Integer.toString(inv);
        medication_info m = new medication_info(title, amount, getDateFromString(date), time, getDateFromString(enddate), medtype,
                frequency, freq,alarmHour, alarmMin, alarmID, dosage, notify, userId, "");

        db.collection("users").document(currentFirebaseUser.getUid()).collection("New Medications")
                .document(medication_info.getId()).update("InventoryMeds", m.getInventoryMeds())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        Toast.makeText(intake_confirmation.this, "Confirmed Intake", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void moveStartDate(){
        getData();
        myDate = getDateFromString(date);
        if (!date.equals(enddate))
        {
            myDate = DateUtil.addDays(myDate, 1);
        }

        String month = (String) DateFormat.format("MM", myDate);
        String day = (String) DateFormat.format("dd", myDate);
        String year = (String) DateFormat.format("yyyy", myDate);

        alarmMonth = Integer.parseInt(month);
        alarmDay = Integer.parseInt(day);
        alarmYear = Integer.parseInt(year);


        if (date.equals(enddate)) {
            db.collection("users").document(currentFirebaseUser.getUid()).collection("New Medications")
                    .document(medication_info.getId()).delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(intake_confirmation.this, "Deleted Alarm", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        } else {
            db.collection("users").document(currentFirebaseUser.getUid()).collection("New Medications")
                    .document(medication_info.getId()).update("StartDate", myDate)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            Toast.makeText(intake_confirmation.this, "Confirmed Intake", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void moveStartDateWeek() {
        getData();
        myDate = getDateFromString(date);
        if (!date.equals(enddate) && daysBetween(dateToCalendar(getDateFromString(date)), dateToCalendar(getDateFromString(enddate))) > 7)
        {
            myDate = DateUtil.addDays(myDate, 7);
        }
        else
        {
            myDate = getDateFromString(enddate);
        }
        String month = (String) DateFormat.format("MM", myDate);
        String day = (String) DateFormat.format("dd", myDate);
        String year = (String) DateFormat.format("yyyy", myDate);

        alarmMonth = Integer.parseInt(month);
        alarmDay = Integer.parseInt(day);
        alarmYear = Integer.parseInt(year);
        if (date.equals(enddate)) {
            db.collection("users").document(currentFirebaseUser.getUid()).collection("New Medications")
                    .document(medication_info.getId()).delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(intake_confirmation.this, "Deleted Alarm", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        } else {
            db.collection("users").document(currentFirebaseUser.getUid()).collection("New Medications")
                    .document(medication_info.getId()).update("StartDate", myDate)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            Toast.makeText(intake_confirmation.this, "Confirmed Intake", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Critical Levels";
            String description = "Alerts for low medication inventory";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("CriticalLevels", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public Date getDateFromString(String dateToSave) {
        try {
            Date date = dateFormat.parse(dateToSave);
            return date;
        } catch (ParseException e) {
            return null;
        }
    }

    public void saveToHistory() {
        getData();
        String Medication = medication_info.getMedication();
        String Time = medication_info.getTime();
        String Qty = medication_info.getInventoryMeds();

        String Expiration = "";
        if(medication_info.getExpiration() != null) {
         Expiration = medication_info.getExpiration();
        }
        if(Expiration.equals("") || Expiration == null) {
            Expiration = "none";
        }

        String StartDate = date;
        Map<String, Object> user = new HashMap<>();
        user.put("Medication", Medication);
        user.put("Time", Time);
        user.put("StartDate",  StartDate);
        user.put("Qty", Qty);
        user.put("Expiration", Expiration);

        db.collection("users").document(userId).collection("Medication History")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(intake_confirmation.this, "New Medication added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onSuccess: failed");
                    }
                });
    }

    private void startAlarm(Calendar c)
    {
        getData();
        String month = (String) DateFormat.format("MM", myDate);
        String day = (String) DateFormat.format("dd", myDate);
        String year = (String) DateFormat.format("yyyy", myDate);

        alarmMonth = Integer.parseInt(month);
        alarmDay = Integer.parseInt(day);
        alarmYear = Integer.parseInt(year);
        myAlarmDate.setTimeInMillis(System.currentTimeMillis());
        myAlarmDate.set(alarmYear, alarmMonth-1, alarmDay, alarmHour, alarmMin);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, alarmreceiver.class);
        PendingIntent pendingDB = PendingIntent.getBroadcast(this, alarmID, intent, 0);
        alarmManager.cancel(pendingDB);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmID, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, myAlarmDate.getTimeInMillis(), pendingIntent);
    }

    private void speak() {


        if(voice != null) {
            if (!voice.isEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    for (Voice tmpVoice : textToSpeech.getVoices()) {
                        if (tmpVoice.getName().equals(voice)) {
                            textToSpeech.setVoice(tmpVoice);
                            break;
                        }
                    }
                }

            }

            textToSpeech.setPitch(pitch);
            textToSpeech.setSpeechRate(speed);
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);

        }
        else {
            Toast.makeText(intake_confirmation.this, "Input is blank", Toast.LENGTH_LONG).show();
        }
    }

    public void intake_To_Today(View view) {
        Intent intent = new Intent(intake_confirmation.this, today_page_recycler.class);
        startActivity(intent);
    }

    public static long daysBetween(Calendar startDate, Calendar endDate) {
        Calendar date = (Calendar) startDate.clone();
        long daysBetween = 0;
        while (date.before(endDate)) {
            date.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }

    private Calendar dateToCalendar(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;

    }

}

class DateUtil {
    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
}


