package com.example.capstone1.v2;

import static com.example.capstone1.home_page.TAG;

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

import java.util.ArrayList;
import java.util.Base64;

public class take_medication extends AppCompatActivity {
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    FirebaseAuth rootAuthen;
    String userId;
    FloatingActionButton profileBtn;
    long accounttype;
    String chosenMedication;
    Spinner spinner;
    TextView type;
    EditText dosages;

    Button take;
    ArrayList<medication_info> medicationList;
    ArrayList<String> options;
    String tMedicationId;

    boolean isSpinnerTouched = false;

    int pos;

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
            spinner = findViewById(R.id.take_medication_choose);
            dosages = findViewById(R.id.DosageBox);
            type = findViewById(R.id.type_spinner_one);
            take = findViewById(R.id.btntakemedication);

          //clear s


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
                        loadDataSpinner();
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        } catch(Exception e) {
            e.printStackTrace();
        }


        take.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                String intake = dosages.getText().toString();
                if(intake.equals("") || intake.isEmpty() && chosenMedication.equals("") || chosenMedication.isEmpty()) {
                    Toast.makeText(take_medication.this, "Intake/Medicine Name is blank", Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    try {

                        if(Integer.parseInt(intake) <= 0) {
                            Toast.makeText(take_medication.this, "Intake is 0 or Negative", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(Integer.parseInt(medicationList.get(pos).getInventoryMeds()) == 0) {
                            Toast.makeText(take_medication.this, "Medicine inventory is 0 please refill", Toast.LENGTH_LONG).show();
                            return;
                        }

                        int calculateIntake =  Integer.parseInt(medicationList.get(pos).getInventoryMeds()) - Integer.parseInt(intake);
                        if(calculateIntake < 0) {
                            Toast.makeText(take_medication.this, "Intake is greater than inventory, please take only what you have", Toast.LENGTH_LONG).show();
                            return;
                        }

                        String finalIntake = String.valueOf(calculateIntake);
                        fstore.collection("users").document(userId).collection("New Medications").document(tMedicationId).update("InventoryMeds", finalIntake).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(take_medication.this, "Confirmed intake medicine", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(take_medication.this, user_manage_medications.class));
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(take_medication.this, "Failed to take medicine, try again later...", Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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
        Intent intent = new Intent(take_medication.this, optical_character_recognition.class);
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




        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = parent.getItemAtPosition(position).toString();
                Log.d("USERS", value);
                chosenMedication = options.get(position);
                type.setText(medicationList.get(position).getMedicineTypeName());
                tMedicationId = medicationList.get(position).getId();
                pos = position;




            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                chosenMedication = "";

            }
        });

    }


}
