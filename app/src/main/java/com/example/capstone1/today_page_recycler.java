package com.example.capstone1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstone1.v2.chat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class today_page_recycler extends AppCompatActivity {
    RecyclerView recyclerView1, rViewMeasurement;
    ArrayList<medication_info> medicationArrayList;
    MyMedicationAdapter medAdapter;
    public static final String TAG = "TAG";
    ArrayList<measurement_info_today> measurementArrayList;
    HMAdapterToday measurementAdapter;
    FirebaseFirestore db;
    String single;
    DateFormat dateFormat = new SimpleDateFormat("M/dd/yyyy");
    static Date dateSelected;
    ProgressDialog progressDialog;
    Button switchDisplay;
    static final SimpleDateFormat format = new SimpleDateFormat("M/dd/yyyy");
    String date;
    FloatingActionButton profileBtn;
    int day;
    medication_info medication_info;
    private CalendarView calendarView;
    int layout = 0;
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    TextView firstname;
    FirebaseAuth rootAuthen;
    String userId;
    long accounttype ;
    ImageView helpDosageEdit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_page_recycler);
        firstname = findViewById(R.id.firstnameview);
        rootAuthen = FirebaseAuth.getInstance();
        userId = rootAuthen.getCurrentUser().getUid();
        switchDisplay = findViewById(R.id.switchToday);
        profileBtn = findViewById(R.id.Profile_page);

        helpDosageEdit = findViewById(R.id.helpDosageEdit);


        helpDosageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(today_page_recycler.this);
                aBuilder.setCancelable(true);
                aBuilder.setTitle("Schedule");
                aBuilder.setMessage("Choose a date to display the list of medications that the user will take for that day.\n\n" +
                        "The Change button will switch the shown list from medication schedule to measurement schedule and vise versa");
                aBuilder.show();

            }
        });

        DocumentReference documentReference = fstore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "listen:error", error);
                    firstname.setText(" ");
                    return;
                }
                try{
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        Base64.Decoder decoder = Base64.getDecoder();
                        byte [] bytes =decoder.decode(value.getString("firstname"));
                        firstname.setText(new String(bytes));
                    }
                }catch (Exception e)
                {
                    firstname.setText(" ");
                }
            }
        });
        
        switchDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (layout == 0) {
                        recyclerGone();
                    }
                    else if(layout == 1)
                    {
                        recyclerVisible();

                    }
                }
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
       // progressDialog.show();
        calendarView = findViewById(R.id.calendarViewTD);

        recyclerView1 = findViewById(R.id.recyclerViewToday);
        rViewMeasurement = findViewById(R.id.todayMeasurement);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        rViewMeasurement.setHasFixedSize(true);
        rViewMeasurement.setLayoutManager(new LinearLayoutManager(this));
        db = FirebaseFirestore.getInstance();
        medicationArrayList = new ArrayList<medication_info>();
        medAdapter = new MyMedicationAdapter(today_page_recycler.this, medicationArrayList);
        measurementArrayList = new ArrayList<measurement_info_today>();
        measurementAdapter = new HMAdapterToday(today_page_recycler.this, measurementArrayList);


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                month +=1;
                if (dayOfMonth < 10)
                {
                    single = "0" + dayOfMonth;
                    date = month + "/" + single +"/"+year;
                }
                else
                {
                    date = month + "/" + dayOfMonth +"/"+year;
                }

                dateSelected = getDateFromString(date);
                c.setTime(dateSelected);
                day = c.get(Calendar.DAY_OF_WEEK);
                Log.d("Calendar", "Selected day change " + date );
                //Log.d("Calendar", "Selected day change" + medicationArrayList.size() );

                EventChangeListener();
                measurementEvent();
                medicationArrayList.clear();
                measurementArrayList.clear();
                recyclerView1.setAdapter(medAdapter);
                rViewMeasurement.setAdapter(measurementAdapter);
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentReference.addSnapshotListener(today_page_recycler.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "listen:error", error);
                            firstname.setText(" ");
                            return;
                        }
                        try {
                            accounttype = value.getLong("accounttype");

                            Log.d("TAG", "tag: " + accounttype);
                            if (accounttype == 1)
                            {
                                Intent intent = new Intent(today_page_recycler.this, user_information.class);
                                startActivity(intent);
                            }
                            else if (accounttype == 2)
                            {
                                Intent intent = new Intent(today_page_recycler.this, guestLogout.class);
                                startActivity(intent);
                            }
                        }catch (Exception e){
                            Intent intent = new Intent(today_page_recycler.this, guestLogout.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });

    }
    public void Today_To_Home(View view){
        Intent intent = new Intent(today_page_recycler.this, home_page.class);
        startActivity(intent);
    }
    public void Today_To_User(View view){
        Intent intent = new Intent(today_page_recycler.this, user_information.class);
        startActivity(intent);
    }
    public void Today_To_History(View view){
        Intent intent = new Intent(today_page_recycler.this, history_page.class);
        startActivity(intent);
    }

    public void Today_To_Chat(View view){
        Intent intent = new Intent(today_page_recycler.this, chat.class);
        startActivity(intent);
    }

    public void Today_To_Today(View view){
        Intent intent = new Intent(today_page_recycler.this, today_page_recycler.class);
        startActivity(intent);
    }

    private void EventChangeListener() {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        db.document("users/"+currentFirebaseUser.getUid()).collection("New Medications")
                .orderBy("Medication", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null)
                {
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                    Log.e("Firestore error", e.getMessage());
                    return;
                }
                for(DocumentChange dc : queryDocumentSnapshots.getDocumentChanges())
                {
                    if(dc.getType() == DocumentChange.Type.ADDED) {
                        medication_info m = dc.getDocument().toObject(medication_info.class);
                        m.setId(dc.getDocument().getId());
                        Calendar c = Calendar.getInstance();
                        String strDate = dateFormat.format(m.getStartDate());
                        String strEnd = dateFormat.format(m.getEndDate());
                        c.setTime(m.getStartDate());
                        int start = c.get(Calendar.DAY_OF_WEEK);


                        if(medicationArrayList != null)
                        {
                            if(date.equals(strDate) || date.equals(strEnd) || dateSelected.after(m.getStartDate())
                                    && dateSelected.before(m.getEndDate()) && m.getFrequency() == 1)
                            {
                                medicationArrayList.add(m);
                                Log.d("message", "hello" + m.getStartDate());
                            }
                            else if(date.equals(strDate) || date.equals(strEnd) && m.getFrequency() == 2
                                     || dateSelected.after(m.getStartDate()) && dateSelected.before(m.getEndDate()) && start == day)
                            {
                                medicationArrayList.add(m);
                            }
                        }
                    }
                    medAdapter.notifyDataSetChanged();
                    if(progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }
        });
    }

    private void measurementEvent() {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        db.document("users/"+currentFirebaseUser.getUid()).collection("Health Measurement Alarm")
                .orderBy("HMName", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null)
                {
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                    Log.e("Firestore error", e.getMessage());
                    return;
                }
                for(DocumentChange dc : queryDocumentSnapshots.getDocumentChanges())
                {
                    if(dc.getType() == DocumentChange.Type.ADDED) {
                        measurement_info_today b = dc.getDocument().toObject(measurement_info_today.class);
                        b.setId(dc.getDocument().getId());

                        Calendar c = Calendar.getInstance();
                        String strDate = dateFormat.format(b.getStartDate());
                        String strEnd = dateFormat.format(b.getEndDate());
                        c.setTime(b.getStartDate());
                        int start = c.get(Calendar.DAY_OF_WEEK);
                        if(measurementArrayList != null)
                        {
                            if(date.equals(strDate) || date.equals(strEnd) || dateSelected.after(b.getStartDate())
                                    && dateSelected.before(b.getEndDate()) && b.getFrequency() == 1)
                            {
                                measurementArrayList.add(b);
                                Log.d("message", "hello" + b.getStartDate());
                            }
                            else if(date.equals(strDate) || date.equals(strEnd) && b.getFrequency() == 2
                                    || dateSelected.after(b.getStartDate()) && dateSelected.before(b.getEndDate()) && start == day)
                            {
                                measurementArrayList.add(b);
                            }
                        }
                    }
                    measurementAdapter.notifyDataSetChanged();
                    if(progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }
        });
    }

    public Date getDateFromString(String dateToSave) {
        try {
            Date date = format.parse(dateToSave);
            return date ;
        } catch (ParseException e){
            return null ;
        }
    }

    private void recyclerGone()
    {
        recyclerView1.setVisibility(View.GONE);
        rViewMeasurement.setVisibility(View.VISIBLE);

        layout = 1 ;
    }
    private void recyclerVisible()
    {
        rViewMeasurement.setVisibility(View.GONE);
        recyclerView1.setVisibility(View.VISIBLE);
        layout = 0;
    }
}



