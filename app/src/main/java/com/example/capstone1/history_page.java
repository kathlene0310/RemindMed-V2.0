package com.example.capstone1;

import static com.example.capstone1.home_page.TAG;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone1.v2.refill_medication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableReference;
import com.google.firebase.functions.HttpsCallableResult;


import com.example.capstone1.v2.chat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class history_page extends AppCompatActivity {
    RecyclerView recyclerView1;

    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    TextView firstname, clear, pdf;
    FirebaseAuth rootAuthen;
    String userId;
    long accounttype ;
    FloatingActionButton profileBtn;
    ArrayList<medication_history_info> myArrayList;
    medication_history_adapter myAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    ImageView helpHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_page);
        //firstname = findViewById(R.id.firstnameview);
        rootAuthen = FirebaseAuth.getInstance();
        userId = rootAuthen.getCurrentUser().getUid();
        clear = findViewById(R.id.clearAll_medications);
        profileBtn = findViewById(R.id.profile_history_two);
        pdf = findViewById(R.id.textView54);
        helpHistory = findViewById(R.id.helpHistory);

        helpHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(history_page.this);
                aBuilder.setCancelable(true);
                aBuilder.setTitle("History");
                aBuilder.setMessage("Choose between Measurement or Medication to show the list of taken medicine or measured measurement.\n\n" +
                        "Clear All button will clear all of the history\n\n" +
                        "PDF will print the history details of all the Measurement and Medicine");
                aBuilder.show();

            }
        });

        DocumentReference documentReference = fstore.collection("users").document(userId);
        /*documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
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
        });*/
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
       // progressDialog.setMessage("Fetching Data...");
       // progressDialog.show();

        recyclerView1 = findViewById(R.id.recycleViewHistoryMed);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        db = FirebaseFirestore.getInstance();
        myArrayList = new ArrayList<medication_history_info>();
        myAdapter = new medication_history_adapter(history_page.this, myArrayList);

        recyclerView1.setAdapter(myAdapter);
        EventChangeListener();


        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(history_page.this, pdf_medication.class));
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(history_page.this);
                alert.setTitle("Delete")
                        .setMessage("Are you sure you want to clear your history")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.document("users/" + currentFirebaseUser.getUid()).collection("Medication History")
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (QueryDocumentSnapshot snapshot: task.getResult())
                                        {
                                            db.document("users/" + currentFirebaseUser.getUid()).collection("Medication History")
                                                    .document(snapshot.getId()).delete();
                                        }
                                    }
                                });
                                myAdapter.notifyDataSetChanged();
                                Intent intent = new Intent(history_page.this, history_page.class);
                                startActivity(intent);

                            }
                        });
                alert.show();
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentReference.addSnapshotListener(history_page.this, new EventListener<DocumentSnapshot>() {
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
                                Intent intent = new Intent(history_page.this, user_information.class);
                                startActivity(intent);
                            }
                            else if (accounttype == 2)
                            {
                                Intent intent = new Intent(history_page.this, guestLogout.class);
                                startActivity(intent);
                            }
                        }catch (Exception e){
                            Intent intent = new Intent(history_page.this, guestLogout.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });

    }

    public void History_To_Home(View view) {
        Intent intent = new Intent(history_page.this, home_page.class);
        startActivity(intent);
    }

    public void History_To_Today(View view) {
        Intent intent = new Intent(history_page.this, today_page_recycler.class);
        startActivity(intent);
    }

    public void History_To_User(View view) {
        Intent intent = new Intent(history_page.this, user_information.class);
        startActivity(intent);
    }

    public void History_To_Chat(View view) {
        Intent intent = new Intent(history_page.this, chat.class);
        startActivity(intent);
    }
    public void medHistory_to_measurement(View view){
        Intent intent = new Intent(history_page.this, history_for_measurements.class);
        startActivity(intent);
    }

    public void History_To_History(View view) {
        Intent intent = new Intent(history_page.this, history_page.class);
        startActivity(intent);
    }

    private void EventChangeListener() {

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        db.document("users/" + currentFirebaseUser.getUid()).collection("Medication History").orderBy("StartDate", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Log.e("Firestore error", e.getMessage());
                    return;
                }

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        myArrayList.add(dc.getDocument().toObject(medication_history_info.class));
                    }

                    myAdapter.notifyDataSetChanged();
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }
        });
    }
}