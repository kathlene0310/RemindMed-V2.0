package com.example.capstone1.v2;

import static com.example.capstone1.home_page.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.example.capstone1.R;
import com.example.capstone1.dependent.User;
import com.example.capstone1.dependent.UserAdapter;
import com.example.capstone1.guestLogout;
import com.example.capstone1.health_measurements;
import com.example.capstone1.history_for_measurements;
import com.example.capstone1.home_page;
import com.example.capstone1.main_page;
import com.example.capstone1.today;
import com.example.capstone1.history_page;
import com.example.capstone1.user_information;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Base64;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class send_report extends AppCompatActivity {
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    FirebaseAuth rootAuthen;
    String userId;
    FloatingActionButton profileBtn;
    Button sendReport, cancel;
    long accounttype ;
    Spinner spinner;
    TextView txtEmail;
    Button chatRedirect;
    ImageView helpSendReport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_send_report);


        rootAuthen = FirebaseAuth.getInstance();
        userId = rootAuthen.getCurrentUser().getUid();

        sendReport = findViewById(R.id.btnSendReport);
        cancel = findViewById(R.id.cancel_bs_later);
        spinner = findViewById(R.id.frequency_set_later_bs);
        txtEmail = findViewById(R.id.txtEmail);
        helpSendReport = findViewById(R.id.helpSendReport);
        sendReport.setEnabled(false);


        helpSendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(send_report.this);
                aBuilder.setCancelable(true);
                aBuilder.setTitle("Send Report");
                aBuilder.setMessage("Clicking the send report button will send a PDF version of the measurement and medications of the user.");
                aBuilder.show();

            }
        });


        sendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestQueue volleyQueue = Volley.newRequestQueue(send_report.this);
                String url = "https://18.183.57.223.nip.io/post"; //AMAZON SERVER

                try {
                    JSONObject obj = new JSONObject();
                    String email = txtEmail.getText().toString();


                    obj.put("userId", userId);


                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        obj,

                        (Response.Listener<JSONObject>) response -> {
                            sendReport.setEnabled(true);
                            String res;
                            try {
                                res = response.getString("message");
                                Toast.makeText(send_report.this, res, Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                Toast.makeText(send_report.this, "No response from the server, please try again later", Toast.LENGTH_LONG).show();
                            }
                        },

                        (Response.ErrorListener) error -> {
                            sendReport.setEnabled(true);
                            Toast.makeText(send_report.this, "Check your internet connection, some error occurred! " + error.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                            Log.e("test", "Error: ${error.localizedMessage}" + error.getLocalizedMessage());
                        }
                );

                    jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                            0,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                volleyQueue.add(jsonObjectRequest);
                sendReport.setEnabled(false);

                    sendReport.setEnabled(false);


                }
                catch(Exception e) {
                    e.printStackTrace();
                    Log.e("test", "Error:" + e);
                }


                /*
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendReport.setEnabled(true);
                        Toast.makeText(send_report.this, "Request timeout, try again later", Toast.LENGTH_LONG).show();
                    }
                },15000);


                 */
            }


        });


        try {
            DocumentReference df = fstore.collection("users").document(userId);
            df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            try {
                                Log.d("TESTTEST", document.getData().get("email").toString());


                                if(document.get("dependent") != null) {
                                    String dependentId = document.get("dependent").toString();
                                    if(!dependentId.isEmpty()) {
                                        getDependentEmail(dependentId);
                                    }
                                }



                            } catch (Exception e) {
                                Log.d("E", "e" + e);
                            }
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        } catch (Exception e){
            Log.d("E", "e" + e);
        }


        //spinner.setPrompt("");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(send_report.this, user_information.class));
            }
        });



    }
    public void Send_To_Home(View view) {
        Intent intent = new Intent(send_report.this, home_page.class);
        startActivity(intent);
    }

    public void getDependentEmail(String dependentId) {
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

                    if(snapshot.get("email") != null) {
                        txtEmail.setText(snapshot.get("email").toString());
                        sendReport.setEnabled(true);
                        //spinner.setPrompt(snapshot.get("email").toString());
                    }
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }
    public void Send_To_Today(View view) {
        Intent intent = new Intent(send_report.this, today.class);
        startActivity(intent);
    }

    public void Send_To_History(View view) {
        Intent intent = new Intent(send_report.this, history_page.class);
        startActivity(intent);
    }

    public void Send_To_Chat(View view) {
        Intent intent = new Intent(send_report.this, chat.class);
        startActivity(intent);
    }

}
