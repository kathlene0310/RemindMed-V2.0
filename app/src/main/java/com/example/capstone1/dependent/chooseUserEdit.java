package com.example.capstone1.dependent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone1.R;
import com.example.capstone1.v2.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.capstone1.main_page;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Base64;

public class chooseUserEdit extends AppCompatActivity {

    FloatingActionButton manage, view, home, chat;
    Button submit;
    Spinner spinner;
    String userChosenId = "";
    ArrayList<String> options;
    ArrayList<String> users;
    String userId;
    FirebaseAuth rootAuthen;
    FirebaseFirestore fstore;
    String [] finalArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_dependent_choose_user_for_editmedication);


        Firebase firebase  = new Firebase(getApplicationContext());

        fstore = firebase.getFstore();
        userId = firebase.getUserId();
        rootAuthen = firebase.getRootAuthen();

        options = new ArrayList<String>();
        users = new ArrayList<String>();

        submit = findViewById(R.id.loginBtn);
        spinner = findViewById(R.id.choose_user_spinner);




        DocumentReference df = fstore.collection("users").document(userId);

        df.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                try
                {
                    ArrayList<String> userIds = (ArrayList<String>) value.get("users");
                    Log.d("ARRAY", "DATA" + userIds);


                    for (String user : userIds) {
                        DocumentReference df = fstore.collection("users").document(user);

                        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot doc = task.getResult();
                                if (doc.exists()) {
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        Base64.Decoder decoder = Base64.getDecoder();
                                        byte[] bytesFN = decoder.decode(doc.get("firstname").toString());
                                        byte[] bytesLN = decoder.decode(doc.get("lastname").toString());
                                        String u  = doc.get("uid").toString();

                                        String f = new String(bytesFN);
                                        String l = new String(bytesLN);

                                        Log.d("F", "firsstname" + f);
                                        Log.d("F", "lname" + l);
                                        if (f.isEmpty() || l.isEmpty()) {
                                            return;
                                        }

                                        User x = new User(u, f, l);
                                        Log.d("X", "X VAL" + x);
                                        options.add(f + " " + l);
                                        users.add(u);
                                        loadDataSpinner();
                                    }
                                }




                            }
                        });
                    }





                }
                catch(Exception e) {
                    Log.d("ERROR", "ERROR" + e);
                    Toast.makeText(getApplicationContext(), "Unexpected Error, occured please login again", Toast.LENGTH_LONG);
                    startActivity(new Intent(getApplicationContext(), main_page.class));
                }
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                if(userChosenId.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please choose a user", Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    Intent i = new Intent(chooseUserEdit.this, manageMedications.class);

                    i.putExtra("USER_CHOSEN", userChosenId);
                    startActivity(i);
                } }
                catch(Exception e) {
                    Log.d("EXCEPTION", "ERROR " + e);
                    Toast.makeText(getApplicationContext(), "Please choose a user first", Toast.LENGTH_LONG).show();
                }
            }
        });



    }


    public void Choose_To_Home (View view) {
        Intent i = new Intent(chooseUserEdit.this, home.class);
        startActivity(i);
    }

    public void loadDataSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = parent.getItemAtPosition(position).toString();
                Log.d("USERS", value);
                userChosenId = users.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                userChosenId = "";
            }
        });

    }



}