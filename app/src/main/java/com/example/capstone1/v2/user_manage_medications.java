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
import com.example.capstone1.new_medications;
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

public class user_manage_medications extends AppCompatActivity {
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    FirebaseAuth rootAuthen;
    String userId;
    FloatingActionButton profileBtn;
    long accounttype ;

    ImageView add, refill, take;
    TextView txtAdd, txtRefill, txtTake;

    Button chatRedirect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_take_and_refill_medication);


        add = findViewById(R.id.imageView22);
        refill = findViewById(R.id.imageView23);
        take = findViewById(R.id.imageView24);


        txtAdd = findViewById(R.id.textView52);
        txtRefill = findViewById(R.id.textView53);
        txtTake = findViewById(R.id.textView51);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Manage_To_Add(v);
            }
        });

        refill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Manage_To_Refill(v);
            }
        });

        take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Manage_To_Take(v);
            }
        });

        txtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Manage_To_Add(v);
            }
        });

        txtRefill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Manage_To_Refill(v);
            }
        });

        txtTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Manage_To_Take(v);
            }
        });



    }

    public void Manage_To_Home(View view) {
        Intent intent = new Intent(user_manage_medications.this, home_page.class);
        startActivity(intent);
    }

    public void Manage_To_Today(View view) {
        Intent intent = new Intent(user_manage_medications.this, today.class);
        startActivity(intent);
    }

    public void Manage_To_History(View view) {
        Intent intent = new Intent(user_manage_medications.this, history_page.class);
        startActivity(intent);
    }

    public void Manage_To_Chat(View view) {
        Intent intent = new Intent(user_manage_medications.this, chat.class);
        startActivity(intent);
    }

    public void Manage_To_Add(View view) {
        Intent intent = new Intent(user_manage_medications.this, new_medications.class);
        startActivity(intent);
    }

    public void Manage_To_Refill(View view) {
        Intent intent = new Intent(user_manage_medications.this, refill_medication.class);
        startActivity(intent);
    }

    public void Manage_To_Take(View view) {
        Intent intent = new Intent(user_manage_medications.this, take_medication.class);
        startActivity(intent);
    }



}
