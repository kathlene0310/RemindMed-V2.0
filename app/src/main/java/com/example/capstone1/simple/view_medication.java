package com.example.capstone1.simple;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone1.R;
import com.example.capstone1.medication_info;
import com.example.capstone1.v2.myHomeAdapterSimple;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class view_medication extends AppCompatActivity {
    Button loginBtn;
    int role = 0;
    String[] options = {"Dependent", "User"};
    RecyclerView recyclerView;
    FirebaseFirestore fstore;
    ArrayList<medication_info> myArrayList;
    ProgressDialog progressDialog;
    myHomeAdapterSimple myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_simple_mode_view_medication);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        recyclerView = findViewById(R.id.recyclerViewHome);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fstore = FirebaseFirestore.getInstance();

        myArrayList = new ArrayList<medication_info>();
        myAdapter = new myHomeAdapterSimple(view_medication.this, myArrayList);


        EventChangeListener();

        recyclerView.setAdapter(myAdapter);


    }




    private void EventChangeListener() {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        fstore.document("users/"+currentFirebaseUser.getUid()).collection("New Medications")
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

    public void VMedication_To_Home(View view) {
        Intent intent = new Intent(this, shome_page.class);
        startActivity(intent);
    }

    public void VMedication_To_Profile(View view) {
        Intent intent = new Intent(this, profile.class);
        startActivity(intent);
    }


}
