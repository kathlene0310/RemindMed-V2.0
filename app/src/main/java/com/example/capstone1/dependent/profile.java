package com.example.capstone1.dependent;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone1.HomeSession;
import com.example.capstone1.R;
import com.example.capstone1.home_page;
import com.example.capstone1.user_information;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.example.capstone1.main_page;
import com.google.firebase.firestore.SetOptions;

public class profile extends AppCompatActivity {

    FirebaseAuth rootAuthen;
    FirebaseFirestore fstore;
    FirebaseUser user;
    String userId;
    Button saveButton;
    EditText editFirstName, editLastName, editPassword, editConfirmPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_dependent_manage_profile_page);

        saveButton = findViewById(R.id.save_bs_later2);
        editFirstName = findViewById(R.id.editTextTextDependentName);
        editLastName = findViewById(R.id.editTextTextDependentName2);
        editPassword = findViewById(R.id.editTextTextPersonName3);
        editConfirmPassword = findViewById(R.id.editTextTextPersonName4);

        try {
            rootAuthen = FirebaseAuth.getInstance();
            fstore = FirebaseFirestore.getInstance();
            userId = rootAuthen.getCurrentUser().getUid();
        }
        catch(Exception e) {
            Log.d("ERROR", "ERROR" + e);
            Toast.makeText(getApplicationContext(), "Session is invalid, please login", Toast.LENGTH_LONG);
            startActivity(new Intent());
        }


        DocumentReference df = fstore.collection("users").document(userId);

        df.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                try
                {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Base64.Decoder decoder = Base64.getDecoder();
                    byte [] bytesFN =decoder.decode(value.getString("firstname"));
                    byte [] bytesLN =decoder.decode(value.getString("lastname"));

                    editFirstName.setText(new String(bytesFN));
                    editLastName.setText(new String(bytesLN));
                }}
                catch(Exception e) {
                    Log.d("ERROR", "ERROR" + e);
                    Toast.makeText(getApplicationContext(), "Unexpected Error, occured please login again", Toast.LENGTH_LONG);
                    startActivity(new Intent(getApplicationContext(), main_page.class));
                }
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String firstname = editFirstName.getText().toString().trim();
                    String lastname = editLastName.getText().toString().trim();
                    String Password = editPassword.getText().toString().trim();
                    String Confirm_Password = editConfirmPassword.getText().toString().trim();

                    Map<String, Object> user = new HashMap<>();
                    if(TextUtils.isEmpty(firstname)) {
                        editFirstName.setError("Firstname is required");
                        return;
                    }
                    if(TextUtils.isEmpty(lastname)) {
                        editLastName.setError("Lastname is required");
                        return;
                    }

                    if(Password.length() > 1) {
                        if (TextUtils.isEmpty(Confirm_Password)) {
                            editConfirmPassword.setError("Confirm Password is required");
                            return;
                        }
                        if (TextUtils.isEmpty(Password)) {
                            editPassword.setError("Password is required");
                            return;
                        }
                        if (Password.length() < 8) {
                            editPassword.setError("Password must be at least 8 characters");
                            return;
                        }
                        if (!Password.equals(Confirm_Password)) {
                            editConfirmPassword.setError("Password does not match");
                            return;
                        }
                        StringBuffer result = computeMD5Hash(Password.toString());
                        user.put("password", result);
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Base64.Encoder encoder = Base64.getEncoder();
                        String encodedName = encoder.encodeToString(firstname.getBytes());
                        String encodedLastName = encoder.encodeToString(lastname.toString().getBytes());
                        user.put("firstname", encodedName);
                        user.put("lastname", encodedLastName);
                    }


                    fstore.collection("users").document(userId).set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Details saved", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                catch(Exception e) {
                    Toast.makeText(getApplicationContext(), "Unexpected Error happened" + e, Toast.LENGTH_LONG);
                }

            }
        });

    }

    public StringBuffer computeMD5Hash(String password)
    {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(password.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer MD5Hash = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[1]);
                while (h.length() < 2)
                    h = "0" + h;
                MD5Hash.append(h);
            }
            return MD5Hash;
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}
