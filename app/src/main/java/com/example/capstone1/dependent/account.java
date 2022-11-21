package com.example.capstone1.dependent;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone1.R;
import com.example.capstone1.main_page;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class account extends AppCompatActivity {
    Button buttonLogout, buttonDeleteAcc;
    FirebaseUser firebaseUser;
    FirebaseAuth rootAuthen;
    FirebaseFirestore fstore;
    TextView dEmail;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_dependent_more);

        buttonLogout = findViewById(R.id.btnLogout);
        buttonDeleteAcc = findViewById(R.id.btnDeleteAccount);
        dEmail = findViewById(R.id.dEmail);
        fstore = FirebaseFirestore.getInstance();




        try {
            rootAuthen = FirebaseAuth.getInstance();
            firebaseUser = rootAuthen.getCurrentUser();
            userId = rootAuthen.getCurrentUser().getUid();
        } catch(Exception e) {
            Toast.makeText(getApplicationContext(), "Unexpected Error happened, please login again", Toast.LENGTH_LONG);
            startActivity(new Intent(getApplicationContext(), main_page.class));
        }


        getEmail();
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(account.this, main_page.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        buttonDeleteAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(account.this);
                dialog.setTitle("Are you sure?");
                dialog.setMessage("Deleting this account will permanently remove your account from the system");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(account.this, "Account Deleted", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(account.this, main_page.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(account.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

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

    }


    public void getEmail() {
        DocumentReference df = fstore.collection("users").document(userId);

        df.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    if(snapshot.get("email") != null) {
                        dEmail.setText(snapshot.get("email").toString());
                    }
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }
    public void Account_To_Account(View view) {
        Intent intent = new Intent(this, account.class);
        startActivity(intent);
    }

    public void Account_To_Home(View view) {
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
    }
}
