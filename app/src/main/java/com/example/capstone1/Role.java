package com.example.capstone1;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.concurrent.Executor;

public class Role {

    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    FirebaseAuth rootAuthen;
    String userId;
    int role;

    public Role() {
        rootAuthen = FirebaseAuth.getInstance();
        userId = rootAuthen.getCurrentUser().getUid();
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getRole() {
        return role;
    }

    public void initRole() {
        DocumentReference documentReference = fstore.collection("users").document(userId);
        Log.d("TAG","UIDuser: "+ userId);


        documentReference.addSnapshotListener((Executor) this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("ERR", "listen:error", error);

                }
                try {
                    int role = Integer.parseInt(value.get("role").toString());
                    setRole(role);
                    Log.d("ROLE", "YOUR ROLE:" + role);
                }
                catch (Exception e) {

                }
            }
        });
    }



}
