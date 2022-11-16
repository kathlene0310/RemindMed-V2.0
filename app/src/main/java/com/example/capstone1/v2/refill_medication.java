package com.example.capstone1.v2;

import static com.example.capstone1.home_page.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone1.R;
import com.example.capstone1.dependent.User;
import com.example.capstone1.dependent.UserAdapter;
import com.example.capstone1.guestLogout;
import com.example.capstone1.health_measurements;
import com.example.capstone1.home_page;
import com.example.capstone1.main_page;
import com.example.capstone1.optical_character_recognition;
import com.example.capstone1.optical_character_recognition_one;
import com.example.capstone1.today;
import com.example.capstone1.history_page;
import com.example.capstone1.user_information;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Base64;

public class refill_medication extends AppCompatActivity {
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    FirebaseAuth rootAuthen;
    String userId;
    FloatingActionButton profileBtn;


    Button chatRedirect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_refill_medication);





    }

    public void Back(View view) {
        Intent intent = new Intent(refill_medication.this, user_manage_medications.class);
        startActivity(intent);
    }

    public void Medication_To_Home(View view) {
        Intent intent = new Intent(refill_medication.this, user_manage_medications.class);
        startActivity(intent);
    }

    public void Medication_To_OCR(View view) {
        Intent intent = new Intent(refill_medication.this, optical_character_recognition.class);
        startActivity(intent);
    }

    public void Medication_To_OCRcount(View view) {
        Intent intent = new Intent(refill_medication.this, optical_character_recognition_one.class);
        startActivity(intent);
    }


}
