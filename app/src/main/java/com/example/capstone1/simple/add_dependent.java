package com.example.capstone1.simple;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone1.Faq;
import com.example.capstone1.R;
import com.example.capstone1.forgot_password;
import com.example.capstone1.user_information;
import com.example.capstone1.v2.take_medication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class add_dependent extends AppCompatActivity {
    Button submit, cancel;
    EditText dEmail;
    FirebaseAuth rootAuthen;
    FirebaseFirestore fstore;
    FirebaseUser firebaseUser;
    String userId;
    String [] options = {"Yes", "No"};
    Spinner spinner;
    Boolean autoReport;
    ImageView helpAddDependent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_simple_mode_add_dependent);
        dEmail = findViewById(R.id.dependentEmail);
        submit = findViewById(R.id.save_bs_later);
        cancel = findViewById(R.id.cancel_bs_later);
        spinner = findViewById(R.id.spinner);
        helpAddDependent = findViewById(R.id.helpAddDependent);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);




        helpAddDependent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder aBuilder = new android.app.AlertDialog.Builder(add_dependent.this);
                aBuilder.setCancelable(true);
                aBuilder.setTitle("Add Dependent");
                aBuilder.setMessage("Adding a dependent will give access to the dependent to add or edit the medicines of their chosen user.\n\n" +
                        "The user and the dependent will be able to communicate through the chat feature of the");
                aBuilder.show();

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = parent.getItemAtPosition(position).toString();

                Log.d("D", value);
                if(value.equals("Yes")) {
                  autoReport = true;
                }
                else if(value.equals("No")) {
                  autoReport = false;
                } else{
                   autoReport = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                autoReport = true;
            }
        });


        try {
            rootAuthen = FirebaseAuth.getInstance();
            fstore = FirebaseFirestore.getInstance();
            firebaseUser = rootAuthen.getCurrentUser();
            userId = rootAuthen.getCurrentUser().getUid();
        }
        catch(Exception e) {
            Log.d("ERR", "ERR" + e);
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = dEmail.getText().toString();
                if (TextUtils.isEmpty(email))
                {
                    Toast.makeText(getApplicationContext(), "Please write a valid email address first", Toast.LENGTH_SHORT).show();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    dEmail.setError("Enter a valid email");
                }
                else {
                    try {
                        DocumentReference docRef = fstore.collection("users").document(userId);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {

                                        String dependent = (String) document.get("dependent");

                                        if(dependent == null || dependent.isEmpty()) {

                                            fstore.collection("users")
                                                    .whereEqualTo("email", email)
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                int count = 0;
                                                                for (QueryDocumentSnapshot document : task.getResult()) {

                                                                    String dependentId = document.getId();
                                                                    Log.d("TAG", document.getId() + " => " + document.getData());

                                                                    Map<String, Object> user = new HashMap<>();
                                                                    user.put("dependent",  document.getId());
                                                                    user.put("sendReport", autoReport);
                                                                    fstore.collection("users").document(userId).set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                addWithUserLengthChecker(dependentId, 2);
                                                                                finish();
                                                                            }
                                                                        }
                                                                    });
                                                                    count++;
                                                                }

                                                                if(count <= 0) {
                                                                    Toast.makeText(getApplicationContext(), "No dependent found at inputted email", Toast.LENGTH_SHORT).show();
                                                                }
                                                            } else {
                                                                Log.d("TAG", "Error getting documents: ", task.getException());
                                                            }
                                                        }
                                                    });
                                        }
                                        else {
                                            //Prompt
                                            AlertDialog.Builder dialog = new AlertDialog.Builder(add_dependent.this);
                                            dialog.setTitle("Existing Dependent Found");
                                            dialog.setMessage("Confirming this will replace your dependent");
                                            dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                                fstore.collection("users")
                                                                        .whereEqualTo("email", email)
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    int count = 0;
                                                                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                                                                        String dependentId = document.getId();
                                                                                        Log.d("TAG", document.getId() + " => " + document.getData());

                                                                                        Map<String, Object> user = new HashMap<>();
                                                                                        user.put("dependent",  document.getId());
                                                                                        fstore.collection("users").document(userId).set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    addWithUserLengthChecker(dependentId, 1);
                                                                                                    finish();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                                        count++;
                                                                                    }

                                                                                    if(count <= 0) {
                                                                                        Toast.makeText(getApplicationContext(), "No dependent found at inputted email", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                } else {
                                                                                    Log.d("TAG", "Error getting documents: ", task.getException());
                                                                                }
                                                                            }
                                                                        });


                                                }
                                            });

                                            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            AlertDialog alertDialog = dialog.create();
                                            alertDialog.show();
                                        }
                                    } else {
                                        Log.d("TAG", "Unexpected Error");
                                    }
                                } else {
                                    Log.d("TAG", "get failed with ", task.getException());
                                }
                            }
                        });



                    }
                    catch(Exception err) {
                        Toast.makeText(getApplicationContext(), "Unexpected server error, please try again later", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }




    public void addWithUserLengthChecker(String dependentId, int choice) {
        if(dependentId != null) {
            DocumentReference dfDependent = fstore.collection("users").document(dependentId);
            dfDependent.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot value = task.getResult();
                        if (value.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + value.getData());


                            if (value.get("users") == null) {

                                Map<String, Object> update = new HashMap<>();
                                List<String> array = new ArrayList<String>();
                                update.put("users", array);
                                fstore.collection("users").document(dependentId).set(update, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d("DEBUG", "CREATED USER FIELD");

                                            //ArrayList<String> userIds = (ArrayList<String>) value.get("users");


                                                DocumentReference dependentRef = fstore.collection("users").document(dependentId);
                                                dependentRef.update("users", FieldValue.arrayUnion(userId)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (choice == 1) {
                                                                    Toast.makeText(getApplicationContext(), "Dependent Replaced", Toast.LENGTH_LONG).show();
                                                                    //userIds.clear();
                                                                } else {
                                                                    Toast.makeText(getApplicationContext(), "Dependent Added", Toast.LENGTH_LONG).show();
                                                                    //userIds.clear();
                                                                }
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(getApplicationContext(), "Failed to add dependent", Toast.LENGTH_LONG).show();
                                                                revertAddedDependent();
                                                                return;
                                                            }
                                                        });



                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Failed to add dependent", Toast.LENGTH_LONG).show();
                                        revertAddedDependent();
                                        return;
                                    }
                                });

                            } else {

                            Log.d("USER FIELD", "UFIELD2" + value.get("users"));
                            if (value.get("users") != null) {
                                ArrayList<String> userIds = (ArrayList<String>) value.get("users");
                                Log.d("USERIDS", "userids: " + userIds.toString());
                                Log.d("SIZE", "size: " + userIds.size());
                                if (userIds.size() > 3) {
                                    Toast.makeText(getApplicationContext(), "Target dependent has already 4 users", Toast.LENGTH_LONG).show();
                                    userIds.clear();
                                    return;
                                } else {
                                    DocumentReference dependentRef = fstore.collection("users").document(dependentId);
                                    dependentRef.update("users", FieldValue.arrayUnion(userId)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (choice == 1) {
                                                        Toast.makeText(getApplicationContext(), "Dependent Replaced", Toast.LENGTH_LONG).show();
                                                        userIds.clear();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Dependent Added", Toast.LENGTH_LONG).show();
                                                        userIds.clear();
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Failed to add dependent", Toast.LENGTH_LONG).show();
                                                    revertAddedDependent();
                                                    return;
                                                }
                                            });

                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to add dependent", Toast.LENGTH_LONG).show();
                                revertAddedDependent();
                                return;
                            }

                        }
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to add dependent", Toast.LENGTH_LONG).show();
                    revertAddedDependent();
                    return;
                }
            });

            /*
            dfDependent.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        return;
                    }

                    if (value != null && value.exists()) {
                        Log.d(TAG, "Current data: " + value.getData());
                        if(value.get("users") != null) {
                            ArrayList<String> userIds = (ArrayList<String>) value.get("users");
                            Log.d("USERIDS", "userids: " + userIds.toString());
                            Log.d("SIZE", "size: " + userIds.size());
                            if(userIds.size() > 3) {
                                Toast.makeText(getApplicationContext(), "Target dependent has already 3 users", Toast.LENGTH_LONG).show();
                            }
                            else {
                                DocumentReference dependentRef = fstore.collection("users").document(dependentId);
                                dependentRef.update("users", FieldValue.arrayUnion(userId));
                                Toast.makeText(getApplicationContext(),"Dependent Replaced", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });
            */
        }
    }

    public void revertAddedDependent() {
        Map<String, Object> update = new HashMap<>();
        update.put("dependent",  "");
        fstore.collection("users").document(userId).set(update, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("DEBUG", "REVERTED");
            }
        });
    }

    public void addUserField(String dependentId) {

    }

    public void Back(View view) {
        finish();
    }

    public void Dependent_To_Home(View view) {
        Intent intent = new Intent(this, shome_page.class);
        startActivity(intent);
    }

    public void Dependent_To_Profile(View view) {
        Intent intent = new Intent(this, profile.class);
        startActivity(intent);
    }
}
