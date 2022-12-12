package com.example.capstone1.simple;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone1.R;
import com.example.capstone1.history_for_measurements;
import com.example.capstone1.home_page;
import com.example.capstone1.main_page;
import com.example.capstone1.change_name;
import com.example.capstone1.v2.send_report;
import com.example.capstone1.user_information;
import com.example.capstone1.v2.tts;
import com.example.capstone1.v2.SharedPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class profile extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText birthyr, height, weight;
    int weightChoice, heightChoice;
    Button buttonSave, buttonLogout, buttonDeleteAcc;
    TextView email, firstname, lastname, faq;
    FirebaseAuth rootAuthen;
    FirebaseFirestore fstore;
    FirebaseUser firebaseUser;
    String userId, genderDB, weightName, heightName;
    Spinner spnWeight, spnHeight;
    Spinner spinner;
    Switch switchSimpleMode;
    Boolean simpleMode;
    SharedPref sf;

    ImageView helpSimpleMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_simple_mode_profile);

        try {
            sf = new SharedPref(getApplicationContext());
            simpleMode = sf.getSharedPreferences().getBoolean("simpleMode", false);
        }catch (Exception e) {
            Log.d("Except", "EXCEPTION" + e);
        }


        email = (TextView) findViewById(R.id.emailview);
        helpSimpleMode = findViewById(R.id.helpSimpleMode);
        spinner = findViewById(R.id.gender_spinner);
        birthyr = findViewById(R.id.editTextbirth);
        height = findViewById(R.id.editTextheight);
        weight = findViewById(R.id.editTextweight);
        buttonSave = findViewById(R.id.btnSave);
        buttonLogout = findViewById(R.id.btnLogout);
        buttonDeleteAcc = findViewById(R.id.btnDeleteAcc);
        rootAuthen = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        firebaseUser = rootAuthen.getCurrentUser();
        //faq = (TextView)findViewById(R.id.FAQ);
        spnHeight = findViewById(R.id.spinnerHeight);
        spnWeight = findViewById(R.id.spinnerWeight);
        switchSimpleMode = findViewById(R.id.switchSimpleMode);


        helpSimpleMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder aBuilder = new android.app.AlertDialog.Builder(profile.this);
                aBuilder.setCancelable(true);
                aBuilder.setTitle("Simple Mode");
                aBuilder.setMessage("Switching the button will allow the user to enter simple mode.\n\n" +
                        "First, click the switch then click the back button on the upper left corner of the screen");
                aBuilder.show();

            }
        });



        ArrayAdapter<String> adapterWeight = new ArrayAdapter<String>(profile.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array
                .weight));
        adapterWeight.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnWeight.setAdapter(adapterWeight);

        ArrayAdapter<String> adapterHeight = new ArrayAdapter<String>(profile.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.height));

        adapterWeight.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnHeight.setAdapter(adapterHeight);


        spnWeight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weightChoice = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnHeight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                heightChoice = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        userId = rootAuthen.getCurrentUser().getUid();

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(profile.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.gender));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Gender = spinner.getSelectedItem().toString().trim();
                String Birthyr = birthyr.getText().toString().trim();
                String Height = height.getText().toString().trim();
                String Weight = weight.getText().toString().trim();
                heightName = spnHeight.getSelectedItem().toString();
                weightName = spnWeight.getSelectedItem().toString();

                if (spinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(getApplicationContext(), "Please select gender", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> user = new HashMap<>();
                user.put("gender", Gender);
                user.put("birthyr", Birthyr);
                user.put("height", Height);
                user.put("weight", Weight);
                user.put("weightChoice", weightChoice);
                user.put("weightName", weightName);

                user.put("heightChoice", heightChoice);
                user.put("heightName", heightName);


                fstore.collection("users").document(userId).set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(profile.this, "User information added", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        DocumentReference documentReference = fstore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "listen:error", error);
                    return;
                }
                heightName = value.getString("heightName");
                weightName = value.getString("weightName");
                if(spnHeight!=null)
                {
                    int pos = adapterHeight.getPosition(heightName);
                    spnHeight.setSelection(pos);
                }

                if(spnWeight!=null)
                {
                    int pos = adapterWeight.getPosition(weightName);
                    spnWeight.setSelection(pos);
                }



                email.setText(value.getString("email"));
                //firstname.setText(value.getString("firstname"));
                //lastname.setText(value.getString("lastname"));
                genderDB = (value.getString("gender"));
                if (genderDB != null) {
                    int pos = myAdapter.getPosition(genderDB);
                    spinner.setSelection(pos);
                }
                birthyr.setText(value.getString("birthyr"));
                height.setText(value.getString("height"));
                weight.setText(value.getString("weight"));
            }
        });


        //delete account
        buttonDeleteAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(profile.this);
                dialog.setTitle("Are you sure?");
                dialog.setMessage("Deleting this account will permanently remove your account from the system");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(profile.this, "Account Deleted", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(profile.this, main_page.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(profile.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                    }
                });

                dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });


        //logout
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(profile.this, main_page.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        switchSimpleMode.setChecked(sf.getSimpleMode());

        switchSimpleMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sf.setSimpleMode(true);
                } else {
                    sf.setSimpleMode(false);
                }
            }
        });

    }

    public void SProfile_To_Home(View view) {
        Intent intent = new Intent(this, shome_page.class);
        startActivity(intent);
    }

    public void SProfile_To_Profile(View view) {
        Intent intent = new Intent(this, profile.class);
        startActivity(intent);
    }

    public void SProfile_To_AccountSettings(View view) {
        Intent intent = new Intent(this, change_name.class);
        startActivity(intent);
    }

    public void SProfile_To_SendReport(View view) {
        Intent intent = new Intent(this, send_report.class);
        startActivity(intent);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            startActivity(new Intent(getApplicationContext(), home_page.class));
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onBackPressed() {

        startActivity(new Intent(getApplicationContext(), home_page.class));

        return;
    }
}
