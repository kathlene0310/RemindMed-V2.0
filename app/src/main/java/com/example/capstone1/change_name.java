package com.example.capstone1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone1.v2.SharedPref;
import com.example.capstone1.v2.tts;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class change_name extends AppCompatActivity {
    FirebaseAuth rootAuthen;
    FirebaseFirestore fstore;
    FirebaseUser user;
    Button savebtn;
    EditText editfirstname, editlastname, editemail, mem, editSnooze;
    public static final String TAG = "TAG";
    String userId;
    SharedPref sf;
    int snooze;
    Spinner spinner, spinner2;
    String alarm_sound, alarm_vibration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        try {
            Intent data = getIntent();
            String firstname = data.getStringExtra("firstname");
            String lastname = data.getStringExtra("lastname");
            String email = data.getStringExtra("email");

            rootAuthen = FirebaseAuth.getInstance();
            fstore = FirebaseFirestore.getInstance();
            user = rootAuthen.getCurrentUser();

            sf = new SharedPref(getApplicationContext());

            editSnooze = findViewById(R.id.editSnoozes2);
            editfirstname = findViewById(R.id.editfirstname);
            editlastname = findViewById(R.id.editlastname);
            editemail = findViewById(R.id.editemail);
            spinner = findViewById(R.id.alarmsound_spinner3);
            spinner2 = findViewById(R.id.alarmsound_spinner);
            savebtn = findViewById(R.id.buttonsave);


            try {
                snooze = Integer.parseInt(sf.getSnooze());
            } catch (Exception e) {

            }

            editSnooze.setText(String.valueOf(snooze));


            userId = rootAuthen.getCurrentUser().getUid();
            sf = new SharedPref(getApplicationContext());


            ArrayAdapter<String> adapterUnit = new ArrayAdapter<String>(change_name.this,
                    android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.alarm_sounds));

            adapterUnit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapterUnit);

            spinner.setSelection(adapterUnit.getPosition(sf.getAlarmSound()));

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    alarm_sound = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            ArrayAdapter<String> adapterUnit2 = new ArrayAdapter<String>(change_name.this,
                    android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.alarm_vibrations));

            adapterUnit2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner2.setAdapter(adapterUnit2);

            spinner2.setSelection(adapterUnit2.getPosition(sf.getAlarmVibration()));

            spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    alarm_vibration = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            savebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editfirstname.getText().toString().isEmpty() || editlastname.getText().toString().isEmpty() || editemail.getText().toString().isEmpty() || editSnooze.getText().toString().isEmpty()) {
                        Toast.makeText(change_name.this, "Fields are empty", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    String email = editemail.getText().toString();
                    String snooze = editSnooze.getText().toString();

                    if(alarm_sound != null) {
                        Log.d("ALARM SOUND", alarm_sound);
                        sf.setAlarmSound(alarm_sound);
                    }

                    if(alarm_vibration !=null) {
                        sf.setAlarmVibration(alarm_vibration);
                    }
                    sf.setSnooze(snooze);
                    user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            DocumentReference docRef = fstore.collection("users").document(user.getUid());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                Base64.Encoder encoder = Base64.getEncoder();
                                String encodedName = encoder.encodeToString(editfirstname.getText().toString().getBytes());
                                String encodedLastName = encoder.encodeToString(editlastname.getText().toString().getBytes());
                                Map<String, Object> edited = new HashMap<>();
                                edited.put("email", email);
                                edited.put("firstname", encodedName);
                                edited.put("lastname", encodedLastName);


                                docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(change_name.this, "Updated", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), home_page.class));
                                        finish();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(change_name.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });


            Log.d(TAG, "onCreate: " + firstname + " " + lastname);

            DocumentReference documentReference = fstore.collection("users").document(userId);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        Base64.Decoder decoder = Base64.getDecoder();
                        byte[] bytesFN = decoder.decode(value.getString("firstname"));
                        byte[] bytesLN = decoder.decode(value.getString("lastname"));

                        editemail.setText(value.getString("email"));
                        editfirstname.setText(new String(bytesFN));
                        editlastname.setText(new String(bytesLN));
                    }


                }
            });
        } catch(Exception e)  {
            e.printStackTrace();
        }

    }
    public void Change_To_User (View view){
        Intent intent = new Intent(change_name.this, user_information.class);
        startActivity(intent);
    }
    public void Change_To_Reset (View view){
        Intent intent = new Intent(change_name.this, change_password.class);
        startActivity(intent);
    }
    public void Change_To_TTS (View view){
        Intent intent = new Intent(change_name.this, tts.class);
        startActivity(intent);
    }
}