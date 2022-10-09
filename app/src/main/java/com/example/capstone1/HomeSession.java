package com.example.capstone1;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.capstone1.dependent.home;

public class HomeSession extends Application {
    //FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    String userId;
    int role = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseAuth rootAuthen = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = rootAuthen.getCurrentUser();
        FirebaseFirestore fstore = FirebaseFirestore.getInstance();


        if(firebaseUser !=null) {
            userId = rootAuthen.getCurrentUser().getUid();
            DocumentReference df = fstore.collection("users").document(userId);
            df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {

                        try {
                            int r = Integer.parseInt(doc.get("role").toString());

                            role = r;
                            Log.d("DATA", "TEST" + role);


                            if (role == 1) {
                                Intent intent = new Intent(HomeSession.this, home.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(HomeSession.this, home_page.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                //startActivity(new Intent(HomeSession.this, home_page.class));
                            }
                        }
                        catch(NullPointerException e) {
                            Log.d("EXCEPTION", "User may be a guest, ERROR" + e);
                            Intent intent = new Intent(HomeSession.this, home_page.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }

                    }
                }
            });
        }



    }
}