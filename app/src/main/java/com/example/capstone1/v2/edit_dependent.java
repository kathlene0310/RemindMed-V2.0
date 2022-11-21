package com.example.capstone1.v2;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstone1.R;
import com.example.capstone1.dependent.home;
import com.example.capstone1.history_page;
import com.example.capstone1.home_page;
import com.example.capstone1.today_page_recycler;
import com.example.capstone1.user_information;
import com.example.capstone1.v2.MessageAdapter;
import com.example.capstone1.v2.Messages;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class edit_dependent extends AppCompatActivity {

    FirebaseAuth rootAuthen;
    FirebaseFirestore fstore;
    String userId, dependentId;
    TextView email;
    Button remove;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_edit_dependent);

        try {
            rootAuthen = FirebaseAuth.getInstance();
            fstore = FirebaseFirestore.getInstance();
            userId = rootAuthen.getCurrentUser().getUid();
            email = findViewById(R.id.dFirstname2);
            remove = findViewById(R.id.changeLayout3);

            getDependentId();
            //getDependentEmail();

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDependent();
                }
            });
        } catch(Exception e) {
            e.printStackTrace();
        }


    }

    public void deleteDependent() {
        DocumentReference df = fstore.collection("users").document(userId);

        Map<String,Object> updates = new HashMap<>();
        updates.put("dependent", FieldValue.delete());
        updates.put("sendReport", false);

        df.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                deleteDependentBindedUser();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Toast.makeText(edit_dependent.this, "Failed to remove dependent", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void deleteDependentBindedUser() {
        if(dependentId != null) {
            DocumentReference userDF = fstore.collection("users").document(userId);
            DocumentReference df = fstore.collection("users").document(dependentId);

            df.update("users", FieldValue.arrayRemove(userId)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(edit_dependent.this, "Successfully removed dependent", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    Toast.makeText(edit_dependent.this, "Failed to remove dependent", Toast.LENGTH_LONG).show();
                    //Revert everything if fail
                    userDF.update("dependent", dependentId);
                }
            });
        }
    }

    public void getDependentEmail() {

            DocumentReference df = fstore.collection("users").document(dependentId);
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
                        email.setText(snapshot.get("email").toString());
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });

    }

    public void getDependentId () {
        DocumentReference df  = fstore.collection("users").document(userId);
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

                    if(snapshot.get("dependent") != null) {
                        dependentId = snapshot.get("dependent").toString();
                        getDependentEmail();
                    }
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

    }

    public void Edit_To_Home(View view) {
        startActivity(new Intent(edit_dependent.this, home_page.class));

    }

    public void Edit_To_Today(View view) {
        startActivity(new Intent(edit_dependent.this, today_page_recycler.class));

    }

    public void Edit_To_History(View view) {
        startActivity(new Intent(edit_dependent.this, history_page.class));

    }

    public void Edit_To_Chat(View view) {
        startActivity(new Intent(edit_dependent.this, chat.class));

    }

    public void Edit_To_Profile(View view) {
        startActivity(new Intent(edit_dependent.this, user_information.class));

    }

}


