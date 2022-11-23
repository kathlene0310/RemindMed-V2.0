package com.example.capstone1.simple;

import static android.content.ContentValues.TAG;

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

import com.example.capstone1.R;
import com.example.capstone1.history_page;
import com.example.capstone1.home_page;
import com.example.capstone1.today_page_recycler;
import com.example.capstone1.user_information;
import com.example.capstone1.v2.chat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class edit_dependent_simple extends AppCompatActivity {

    FirebaseAuth rootAuthen;
    FirebaseFirestore fstore;
    String userId, dependentId;
    TextView email;
    Button remove;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_simple_mode_edit_dependent);

        try {
            rootAuthen = FirebaseAuth.getInstance();
            fstore = FirebaseFirestore.getInstance();
            userId = rootAuthen.getCurrentUser().getUid();
            email = findViewById(R.id.dFirstname2);
            remove = findViewById(R.id.changeLayout3);

            getDependentId();
            //getDependentEmail();

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDependent();
                }
            });
        } catch(Exception e) {
            e.printStackTrace();
        }


    }

    public void deleteDependent() {
        DocumentReference df = fstore.collection("users").document(userId);

        Map<String,Object> updates = new HashMap<>();
        updates.put("dependent", FieldValue.delete());
        updates.put("sendReport", false);

        df.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                deleteDependentBindedUser();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Toast.makeText(edit_dependent_simple.this, "Failed to remove dependent", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void deleteDependentBindedUser() {
        if(dependentId != null) {
            DocumentReference userDF = fstore.collection("users").document(userId);
            DocumentReference df = fstore.collection("users").document(dependentId);

            df.update("users", FieldValue.arrayRemove(userId)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(edit_dependent_simple.this, "Successfully removed dependent", Toast.LENGTH_LONG).show();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    Toast.makeText(edit_dependent_simple.this, "Failed to remove dependent", Toast.LENGTH_LONG).show();
                    //Revert everything if fail
                    userDF.update("dependent", dependentId);
                }
            });
        }
    }

    public void getDependentEmail() {

        if(dependentId != null && !dependentId.equals("")) {
            DocumentReference df = fstore.collection("users").document(dependentId);
            df.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        email.setText(snapshot.get("email").toString());
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });
        }

    }

    public void getDependentId () {

        DocumentReference df  = fstore.collection("users").document(userId);
        df.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());

                    if(snapshot.get("dependent") != null) {
                        dependentId = snapshot.get("dependent").toString();
                        if(dependentId != null && !dependentId.equals("")) {
                            getDependentEmail();
                        }
                    }
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

    }

    public void Edit_To_Home(View view) {
        startActivity(new Intent(edit_dependent_simple.this, shome_page.class));

    }

    public void Edit_To_Profile(View view) {
        startActivity(new Intent(edit_dependent_simple.this, profile.class));

    }


}


