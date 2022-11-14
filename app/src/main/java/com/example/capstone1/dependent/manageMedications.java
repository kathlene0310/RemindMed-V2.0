package com.example.capstone1.dependent;


import static com.example.capstone1.intake_confirmation.dateFormat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone1.R;
import com.example.capstone1.guestLogout;
import com.example.capstone1.history_page;
import com.example.capstone1.main_page;
import com.example.capstone1.measurement_info_today;
import com.example.capstone1.medication_info;
import com.example.capstone1.myHomeAdapaterMeasurement;
import com.example.capstone1.myHomeAdpater;
import com.example.capstone1.new_medications;
import com.example.capstone1.schedule_measurements;
import com.example.capstone1.simple.add_dependent;
import com.example.capstone1.today_page_recycler;
import com.example.capstone1.user_information;
import com.example.capstone1.v2.SharedPref;
import com.example.capstone1.simple.shome_page;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import com.example.capstone1.v2.chat;

import java.util.ArrayList;
import java.util.Base64;

public class manageMedications extends AppCompatActivity {
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    FirebaseAuth rootAuthen;
    public static final String TAG = "TAG";
    String userId;
    TextView firstname;
    myHomeAdapter2 myAdapter;
    myHomeAdapaterMeasurement measurementAdapter;
    RecyclerView recyclerView, recyclerviewMeasurement;
    ArrayList<medication_info> myArrayList;
    FloatingActionButton profileBtn;
    ArrayList<measurement_info_today> myMeasurementArrayList;
    ProgressDialog progressDialog;
    Button addMed, addHM, changeLayout, changeLayout2, switchMeasurement, addDependent, editDependent;
    Boolean simpleMode;
    SharedPref sf;
    int layout = 1;
    int recyclerlayout = 1;
    long accounttype;
    int newLayout = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_manage_medications);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                userId= null;
            } else {
                userId= extras.getString("USER_CHOSEN");
            }
        } else {
            userId= (String) savedInstanceState.getSerializable("USER_CHOSEN");
        }


        //setContentView(R.layout.activity_home_page);


        addMed = (Button) findViewById(R.id.add_medications_btn);
        // addHM = (Button) findViewById(R.id.add_measurements_btn);
        profileBtn = findViewById(R.id.profile_history);
        changeLayout = (Button) findViewById(R.id.changeLayout);
        changeLayout2 = (Button) findViewById(R.id.changeLayout2);
        //switchMeasurement = (Button) findViewById(R.id.switchMeasurement);
        //addDependent = findViewById(R.id.add_dependent);
        //editDependent = findViewById(R.id.edit_dependent);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        // progressDialog.show();
        firstname = findViewById(R.id.firstnameview);

        try {
            rootAuthen = FirebaseAuth.getInstance();
            //userId = rootAuthen.getCurrentUser().getUid();
        } catch (Exception e) {
            Log.d("TAG", "EXCEPTION" + e);
            Toast.makeText(getApplicationContext(), "Unexpected Error occurred, please login again", Toast.LENGTH_LONG).show();
            userId = "";
        }


        DocumentReference documentReference = fstore.collection("users").document(userId);
        Log.d("TAG", "UIDuser: " + userId);


        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "listen:error", error);
                    firstname.setText(" ");
                    return;
                }

                try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        Base64.Decoder decoder = Base64.getDecoder();
                        byte[] bytes = decoder.decode(value.getString("firstname"));
                        firstname.setText(new String(bytes));

                    }

                } catch (Exception e) {
                    firstname.setText(" ");
                }

            }
        });



        recyclerView = findViewById(R.id.recyclerViewHome);
        recyclerviewMeasurement = findViewById(R.id.recyclerViewHomeMeasurement);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        myArrayList = new ArrayList<medication_info>();
        myAdapter = new myHomeAdapter2(manageMedications.this, myArrayList);

        recyclerviewMeasurement.setHasFixedSize(true);
        recyclerviewMeasurement.setLayoutManager(new LinearLayoutManager(this));

        myMeasurementArrayList = new ArrayList<measurement_info_today>();
        measurementAdapter = new myHomeAdapaterMeasurement(manageMedications.this, myMeasurementArrayList);

        EventChangeListener();
        measureEventChangeListener();
        recyclerviewMeasurement.setAdapter(measurementAdapter);


        changeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout == 0) {
                    recyclerGone();
                }
                else if(layout == 1)
                {
                    recyclerVisible();
                    recyclerView.setAdapter(myAdapter);
                    //switchMeasurement.setText("Measurements");

                }
            }
        });

        changeLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerGone();
            }
        });

        /*
        switchMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recyclerlayout == 1)
                {
                    switchRecyclerMeasurement();
                    switchMeasurement.setText("Medication");
                }
                else
                {
                    switchRecyclerMedication();
                    switchMeasurement.setText("Measurements");


                }
            }
        });
        */


        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentReference.addSnapshotListener(manageMedications.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "listen:error", error);
                            firstname.setText(" ");
                            return;
                        }
                        try {
                            accounttype = value.getLong("accounttype");
                            Log.d("TAG","ID: "+ accounttype);

                            Log.d("TAG", "tag: " + accounttype);
                            if (accounttype == 1)
                            {
                                Intent intent = new Intent(manageMedications.this, user_information.class);
                                startActivity(intent);
                            }
                            else if (accounttype == 2)
                            {
                                Intent intent = new Intent(manageMedications.this, guestLogout.class);
                                startActivity(intent);
                            }
                        }catch (Exception e){
                            Intent intent = new Intent(manageMedications.this, guestLogout.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });


    }

    public void Home_To_Medication(View view) {
        Intent intent = new Intent(this, newMedications.class);
        intent.putExtra("USER_CHOSEN", userId);
        startActivity(intent);
    }

    public void Home_To_Health(View view) {
        Intent intent = new Intent(this, schedule_measurements.class);
        startActivity(intent);
    }

    public void Home_To_User(View view) {
        Intent intent = new Intent(this, profile.class);
        startActivity(intent);
    }

    public void Home_To_History(View view) {
        Intent intent = new Intent(manageMedications.this, history_page.class);
        startActivity(intent);
    }

    public void Home_To_Today(View view) {
        Intent intent = new Intent(manageMedications.this, today_page_recycler.class);
        startActivity(intent);
    }

    public void Home_To_Home(View view) {
        Intent intent = new Intent(manageMedications.this, home.class);
        startActivity(intent);
    }

    public void Home_To_Chat(View view) {
        Intent intent = new Intent(manageMedications.this, chat.class);
        startActivity(intent);
    }

    public void Home_To_AddDepedendent(View view) {
        Intent intent = new Intent(manageMedications.this, add_dependent.class);
        startActivity(intent);
    }

    private void EventChangeListener() {

        fstore.document("users/"+userId).collection("New Medications")
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
                                m.setUserId(userId);
                                myArrayList.add(m);

                            }
                            myAdapter.notifyDataSetChanged();
                            if(progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    }
                });
    }

    private void measureEventChangeListener() {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        fstore.document("users/"+currentFirebaseUser.getUid()).collection("Health Measurement Alarm")
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

                                measurement_info_today m = dc.getDocument().toObject(measurement_info_today.class);
                                m.setId(dc.getDocument().getId());
                                myMeasurementArrayList.add(m);

                            }
                            measurementAdapter.notifyDataSetChanged();
                            if(progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    }
                });
    }


    private void recyclerGone()
    {
        recyclerView.setVisibility(View.GONE);
        recyclerviewMeasurement.setVisibility(View.GONE);
        changeLayout2.setVisibility(View.GONE);
        //switchMeasurement.setVisibility(View.GONE);
        changeLayout.setVisibility(View.VISIBLE);
        addMed.setVisibility(View.VISIBLE);
        //addHM.setVisibility(View.VISIBLE);
        //addDependent.setVisibility(View.VISIBLE);
        //editDependent.setVisibility(View.VISIBLE);

        layout = 1 ;
        recyclerlayout = 1;
    }
    private void recyclerVisible()
    {
        recyclerView.setVisibility(View.VISIBLE);
        changeLayout2.setVisibility(View.VISIBLE);
        //switchMeasurement.setVisibility(View.VISIBLE);
        changeLayout.setVisibility(View.GONE);
        addMed.setVisibility(View.GONE);
        //addHM.setVisibility(View.GONE);
        //addDependent.setVisibility(View.GONE);
        //editDependent.setVisibility(View.GONE);
        layout = 0;
    }

    private void switchRecyclerMeasurement()
    {
        recyclerView.setVisibility(View.GONE);
        recyclerviewMeasurement.setVisibility(View.VISIBLE);
        recyclerlayout = 0;
    }

    private void switchRecyclerMedication()
    {
        recyclerView.setVisibility(View.VISIBLE);
        recyclerviewMeasurement.setVisibility(View.GONE);
        recyclerlayout = 1;
    }
}
