package com.example.capstone1.v2;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.capstone1.main_page;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Firebase {


    FirebaseAuth rootAuthen;
    FirebaseFirestore fstore;
    String userId;

    public Firebase(Context applicationContext) {
        try {
            this.rootAuthen = FirebaseAuth.getInstance();
            this.fstore = FirebaseFirestore.getInstance();
            this.userId = rootAuthen.getCurrentUser().getUid();
        }
        catch(Exception e) {
            Log.d("ERROR", "ERROR" + e);
            Toast.makeText(applicationContext, "Session is invalid, please login", Toast.LENGTH_LONG);
            Intent intent = new Intent(applicationContext, main_page.class);
            FirebaseAuth.getInstance().signOut();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            applicationContext.startActivity(intent);
        }

    }


    public FirebaseAuth getRootAuthen() {
        return rootAuthen;
    }

    public void setRootAuthen(FirebaseAuth rootAuthen) {
        this.rootAuthen = rootAuthen;
    }
    public FirebaseFirestore getFstore() {
        return fstore;
    }

    public void setFstore(FirebaseFirestore fstore) {
        this.fstore = fstore;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}
