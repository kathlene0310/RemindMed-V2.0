package com.example.capstone1.v2;

import static com.example.capstone1.home_page.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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

public class chat extends AppCompatActivity {
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    FirebaseAuth rootAuthen;
    String userId;
    FloatingActionButton profileBtn;
    long accounttype ;

    Button chatRedirect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_chat);


        rootAuthen = FirebaseAuth.getInstance();
        userId = rootAuthen.getCurrentUser().getUid();
        profileBtn = findViewById(R.id.profile_chat);
        chatRedirect = findViewById(R.id.button);

        DocumentReference documentReference = fstore.collection("users").document(userId);
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentReference.addSnapshotListener(chat.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "listen:error", error);
                            return;
                        }
                        try {
                            accounttype = value.getLong("accounttype");
                            Log.d("TAG","ID: "+ accounttype);

                            Log.d("TAG", "tag: " + accounttype);
                            if (accounttype == 1)
                            {
                                Intent intent = new Intent(chat.this, user_information.class);
                                startActivity(intent);
                            }
                            else if (accounttype == 2)
                            {
                                Intent intent = new Intent(chat.this, guestLogout.class);
                                startActivity(intent);
                            }
                        }catch (Exception e){
                            Intent intent = new Intent(chat.this, guestLogout.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });

        DocumentReference df = fstore.collection("users").document(userId);

        df.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                try
                {
                    if(value.get("dependent") != null) {
                        String dependentId = (String) value.get("dependent");
                        Log.d("depedent", "DATA" + dependentId);
                        if (dependentId != null || !dependentId.isEmpty()) {
                            DocumentReference df = fstore.collection("users").document(dependentId);

                            df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot doc = task.getResult();
                                    if (doc.exists()) {
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                            Base64.Decoder decoder = Base64.getDecoder();
                                            byte[] bytesFN = decoder.decode(doc.get("firstname").toString());
                                            byte[] bytesLN = decoder.decode(doc.get("lastname").toString());

                                            String u = doc.get("uid").toString();
                                            String f = new String(bytesFN);
                                            String l = new String(bytesLN);


                                            chatRedirect.setText(f + " " + l);
                                            Log.d("F", "firsstname" + f);
                                            Log.d("F", "lname" + l);
                                            if (f.isEmpty() || l.isEmpty()) {
                                                return;
                                            }

                                            User x = new User(u, f, l);
                                            Log.d("X", "X VAL" + x);

                                        }


                                        String name = doc.get("name").toString();
                                        if (!name.isEmpty() && !dependentId.isEmpty()) {
                                            final String[] image = {"default_image"};
                                            chatRedirect.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
                                                    chatIntent.putExtra("visit_user_id", dependentId);
                                                    chatIntent.putExtra("visit_user_name", name);
                                                    chatIntent.putExtra("visit_image", image[0]);
                                                    startActivity(chatIntent);
                                                }

                                            });
                                        }
                                    }


                                }
                            });

                        } else {
                            chatRedirect.setText("No Dependent Found");
                            chatRedirect.setEnabled(false);
                        }
                    } else {
                        chatRedirect.setText("No Dependent Found");
                        chatRedirect.setEnabled(false);
                    }



                }
                catch(Exception e) {
                    e.printStackTrace();
                    Log.d("ERROR", "ERROR" + e);
                    Toast.makeText(getApplicationContext(), "Unexpected Error, occured please login again", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), main_page.class));
                }

            }
        });


    }
    public void Chat_To_Home(View view) {
        Intent intent = new Intent(chat.this, home_page.class);
        startActivity(intent);
    }

    public void Chat_To_Today(View view) {
        Intent intent = new Intent(chat.this, today.class);
        startActivity(intent);
    }

    public void Chat_To_History(View view) {
        Intent intent = new Intent(chat.this, history_page.class);
        startActivity(intent);
    }

    public void Chat_To_Chat(View view) {
        Intent intent = new Intent(chat.this, chat.class);
        startActivity(intent);
    }

}
