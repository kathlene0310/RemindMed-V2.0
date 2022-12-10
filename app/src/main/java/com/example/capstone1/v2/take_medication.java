package com.example.capstone1.v2;

import static com.example.capstone1.home_page.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.example.capstone1.intake_confirmation;
import com.example.capstone1.main_page;
import com.example.capstone1.medication_info;
import com.example.capstone1.new_medications;
import com.example.capstone1.optical_character_recognition;
import com.example.capstone1.optical_character_recognition_one;
import com.example.capstone1.today;
import com.example.capstone1.history_page;
import com.example.capstone1.user_information;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class take_medication extends AppCompatActivity {
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    FirebaseAuth rootAuthen;
    String userId;
    FloatingActionButton profileBtn;
    long accounttype;
    String chosenMedication;
    Spinner spinner;
    Spinner spinnertypeunit;
    EditText dosage;
    public static EditText medication;

    Button take;
    ArrayList<medication_info> medicationList;
    ArrayList<String> options;
    String tMedicationId;
    FloatingActionButton ocrMedName1, ocrMedName2;

    boolean isSpinnerTouched = false;
    ImageView helpdosage, helptype;
    int pos, typechoice;

    Button chatRedirect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_take_medication);

        try {
            rootAuthen = FirebaseAuth.getInstance();
            userId = rootAuthen.getCurrentUser().getUid();

            medicationList = new ArrayList<medication_info>();
            options = new ArrayList<String>();
            medication = findViewById(R.id.take_medication_choose);
            dosage = findViewById(R.id.DosageBox);
            //type = findViewById(R.id.type_spinner_one);
            spinnertypeunit = findViewById(R.id.type_spinner_one);
            take = findViewById(R.id.btntakemedication);
            ocrMedName1 = findViewById(R.id.ocr_btn);
            ocrMedName2 = findViewById(R.id.ocr_btn_upload);
            helpdosage = findViewById(R.id.dosageHelp);
            helptype = findViewById(R.id.typehelp);


            //clear s



            ArrayAdapter<String> adapterUnit = new ArrayAdapter<String>(take_medication.this,
                    android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.type));

            adapterUnit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnertypeunit.setAdapter(adapterUnit);

            spinnertypeunit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    typechoice = position;
                    if (position == 4)
                    {
                        dosage.setText(String.valueOf(15));
                        dosage.setFocusable(false);
                        dosage.setEnabled(false);
                    }
                    else
                    {
                        dosage.setFocusableInTouchMode(true);
                        dosage.setEnabled(true);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            ocrMedName1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(take_medication.this, optical_character_recognition_take_med.class);
                    intent.putExtra("ocrchoice", 1 );
                    startActivity(intent);
                }
            });

            ocrMedName2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(take_medication.this, ocr_gallery.class);
                    intent.putExtra("ocrchoice", 1 );
                    startActivity(intent);
                }
            });



            helpdosage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder aBuilder = new AlertDialog.Builder(take_medication.this);
                    aBuilder.setCancelable(true);
                    aBuilder.setTitle("Take");
                    aBuilder.setMessage("Enter the amount you will take.\n\n" +
                            "For solids: Enter the amount of pills, capsule or tablets you will take\n\n" +
                            "For liquids: Enter the amount you will take in ml\n\n"+
                            "This will be deducted to the total of your inventory every time you take your medication");
                    aBuilder.show();

                }
            });

            helptype.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder aBuilder = new AlertDialog.Builder(take_medication.this);
                    aBuilder.setCancelable(true);
                    aBuilder.setTitle("Type/Unit");
                    aBuilder.setMessage("The type/unit box is to choose if the medication you are taking will be solid or liquid.\n\n" +
                            "If it is a solid medication you will have three choices:\nPill\nCapsule\nTablet\n\n"+
                            "If it is a liquid medication you will have two choices:\nTablespoon\nML ");
                    aBuilder.show();

                }
            });

            /*
            fstore.collection("users").document(userId).collection("New Medications").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            medication_info obj = document.toObject(medication_info.class);
                            obj.setId(document.getId());
                            medicationList.add(obj);
                            options.add(obj.getMedication());
                        }
                        //loadDataSpinner();
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
            */

        } catch(Exception e) {
            e.printStackTrace();
        }


        take.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String intake = dosage.getText().toString();
                if(intake.equals("") || intake.isEmpty()) {
                    Toast.makeText(take_medication.this, "Intake/Medicine Name is blank", Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    try {

                        if (spinnertypeunit.getSelectedItemPosition() == 0) {
                            Toast.makeText(getApplicationContext(), "Please select type", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(Integer.parseInt(intake) <= 0) {
                            Toast.makeText(take_medication.this, "Intake is 0 or Negative", Toast.LENGTH_LONG).show();
                            return;
                        }


                        //String finalIntake = String.valueOf(calculateIntake);
                        saveToHistory();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });



    }

    public void saveToHistory() {
        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        String Medication = medication.getText().toString();
        String intake = dosage.getText().toString();
        String medicationTypeName = spinnertypeunit.getSelectedItem().toString();
        Map<String, Object> user = new HashMap<>();
        user.put("Medication", Medication);
        user.put("Time", currentTime.toString());
        user.put("StartDate",  intake + " " + medicationTypeName);

        fstore.collection("users").document(userId).collection("Medication History")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(take_medication.this, "Confirmed Intake, added to history", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onSuccess: failed");
                    }
                });
    }


    public void Back(View view) {
        Intent intent = new Intent(take_medication.this, user_manage_medications.class);
        startActivity(intent);
    }

    public void Medication_To_Home(View view) {
        Intent intent = new Intent(take_medication.this, user_manage_medications.class);
        startActivity(intent);
    }

    public void Medication_To_OCR(View view) {
        Intent intent = new Intent(take_medication.this, optical_character_recognition_take_med.class);
        startActivity(intent);
    }

    public void Medication_To_OCRcount(View view) {
        Intent intent = new Intent(take_medication.this, optical_character_recognition_one.class);
        startActivity(intent);
    }

    public void loadDataSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);




        /*
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = parent.getItemAtPosition(position).toString();
                Log.d("USERS", value);
                chosenMedication = options.get(position);
                //type.setText(medicationList.get(position).getMedicineTypeName());
                tMedicationId = medicationList.get(position).getId();
                pos = position;




            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                chosenMedication = "";

            }
        });

         */

    }


}
