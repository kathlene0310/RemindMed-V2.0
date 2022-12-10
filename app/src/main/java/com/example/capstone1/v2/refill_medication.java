package com.example.capstone1.v2;

import static com.example.capstone1.home_page.TAG;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.TimePickerDialog;

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
import com.example.capstone1.medication_info;
import com.example.capstone1.new_medications;
import com.example.capstone1.optical_character_recognition;
import com.example.capstone1.optical_character_recognition_one;
import com.example.capstone1.today;
import com.example.capstone1.history_page;
import com.example.capstone1.user_information;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class refill_medication extends AppCompatActivity{
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    FirebaseAuth rootAuthen;
    String userId, startdate, enddate;
    FloatingActionButton profileBtn;
    final int start = 1;
    final int end = 2;
    Calendar calendar = Calendar.getInstance();;
    String chosenMedication;
    Spinner spinner;
    TextView type, expiration;
    EditText dosages;

    private DatePickerDialog datePickerDialog, datePickerDialog2;

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    Button take, dateButton;
    ArrayList<medication_info> medicationList;
    ArrayList<String> options;
    String tMedicationId;
    FloatingActionButton ocrMedName1;
    ImageView helpdosage, helptype, helpinventory;
    int pos;
    static final SimpleDateFormat format = new SimpleDateFormat("M/dd/yyyy");
    int alarmYear, alarmMonth, alarmDay,alarmHour,alarmMin, choice, typechoice, frequencychoide, alarmID;

    Button chatRedirect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_refill_medication);

        try {
            rootAuthen = FirebaseAuth.getInstance();
            userId = rootAuthen.getCurrentUser().getUid();

            medicationList = new ArrayList<medication_info>();
            options = new ArrayList<String>();
            spinner = findViewById(R.id.refill_medication_choose);
            dosages = findViewById(R.id.DosageBox);
            type = findViewById(R.id.type_spinner_one);
            take = findViewById(R.id.btnrefillmedication);
            dateButton = findViewById(R.id.startButton_date2);
            helpdosage = findViewById(R.id.dosageHelp);
            helptype = findViewById(R.id.typehelp);

            dateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    choice = 1;
                    initDatePicker();
                    openDatePicker();
                }
            });
            //clear s


            helpdosage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder aBuilder = new AlertDialog.Builder(refill_medication.this);
                    aBuilder.setCancelable(true);
                    aBuilder.setTitle("Refill");
                    aBuilder.setMessage("Enter the amount you will refill.\n\n" +
                            "For solids: Enter the amount of pills, capsule or tablets you will refill\n\n" +
                            "For liquids: Enter the amount you will refill in ml\n\n"+
                            "This will be added to the total of your inventory every time you refill your medication");
                    aBuilder.show();

                }
            });
            helptype.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder aBuilder = new AlertDialog.Builder(refill_medication.this);
                    aBuilder.setCancelable(true);
                    aBuilder.setTitle("Type/Unit");
                    aBuilder.setMessage("The type/unit box is to choose if the medication you are taking will be solid or liquid.\n\n" +
                            "If it is a solid medication you will have three choices:\nPill\nCapsule\nTablet\n\n"+
                            "If it is a liquid medication you will have two choices:\nTablespoon\nML ");
                    aBuilder.show();

                }
            });

            fstore.collection("users").document(userId).collection("New Medications").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            medication_info obj = document.toObject(medication_info.class);
                            Log.d(TAG, "EXPIRATION:" +obj.getExpiration());
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
                    Toast.makeText(refill_medication.this, "Refill/Medicine Name is blank", Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    try {

                        if(Integer.parseInt(intake) <= 0) {
                            Toast.makeText(refill_medication.this, "Refill is 0 or negative", Toast.LENGTH_LONG).show();
                            return;
                        }


                        int calculateIntake =  Integer.parseInt(medicationList.get(pos).getInventoryMeds()) + Integer.parseInt(intake);


                        String finalIntake = String.valueOf(calculateIntake);

                        Map<String, Object> update = new HashMap<>();
                        update.put("InventoryMeds", finalIntake);
                        update.put("Expiration", startdate.toString());
                        fstore.collection("users").document(userId).collection("New Medications").document(tMedicationId).set(update, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(refill_medication.this, "Confirmed refill medicine", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(refill_medication.this, user_manage_medications.class));
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(refill_medication.this, "Failed to refill medicine, try again later...", Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });





    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month+=1;
                if (choice == start)
                {

                    startdate = makeDateString(day, month, year);
                    alarmYear = year ;
                    alarmMonth = month - 1;
                    alarmDay = day;
                    dateButton.setText(startdate);
                }

            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
    }

    private String makeDateString(int day, int month, int year)
    {


        if (day<10)
        {
            return month + "/" +"0" +day + "/" + year;
        }
        else
        {
            return month + "/"  +day + "/" + year;
        }

    }

    public void openDatePicker() {
        datePickerDialog.show();
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
        Intent intent = new Intent(refill_medication.this, optical_character_recognition_refill_med.class);
        startActivity(intent);
    }

    public void Medication_To_OCRcount(View view) {
        Intent intent = new Intent(refill_medication.this, optical_character_recognition_one.class);
        startActivity(intent);
    }

    public Date getDateFromString(String dateToSave) {
        try {
            Date date = format.parse(dateToSave);
            return date ;
        } catch (ParseException e){
            return null ;
        }
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
                dateButton.setText(medicationList.get(position).getExpiration());
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
