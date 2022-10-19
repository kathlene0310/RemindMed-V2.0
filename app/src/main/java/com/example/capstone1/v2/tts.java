package com.example.capstone1.v2;

import static com.example.capstone1.home_page.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone1.R;
import com.example.capstone1.guestLogout;
import com.example.capstone1.user_information;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class tts extends AppCompatActivity {
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    FirebaseAuth rootAuthen;
    String userId;
    FloatingActionButton profileBtn;
    long accounttype ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_account_settings_tts_config);



    }
}
