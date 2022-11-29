package com.example.capstone1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;



public class choose_role extends AppCompatActivity {

    Button loginBtn;
    int role = 0;
    String[] options = {"Dependent", "User"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_choose_role);

        try {
            loginBtn = findViewById(R.id.loginBtn);
            Spinner spinner = (Spinner) findViewById(R.id.coursesspinner);


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, options);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String value = parent.getItemAtPosition(position).toString();

                    Log.d("D", value);
                    if (value.equals("Dependent")) {
                        role = 1;
                        Log.d("role", "ROLe" + role);
                    } else if (value.equals("User")) {
                        role = 2;
                    } else {
                        role = 0;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    role = 0;
                }

            });

            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (role != 0) {
                        Intent intent = new Intent(getApplicationContext(), create_account.class);
                        intent.putExtra("role", role);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please choose a role or role maybe invalid", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            });

        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    public void Create_To_Main(View view) {
        Intent intent = new Intent(choose_role.this, main_page.class);
        startActivity(intent);
    }
}
