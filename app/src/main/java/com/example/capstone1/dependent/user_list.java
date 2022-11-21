package com.example.capstone1.dependent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.capstone1.main_page;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Base64;

import com.example.capstone1.dependent.User;
public class user_list extends AppCompatActivity {

    ArrayList<User> users = new ArrayList<User>();
    FirebaseAuth rootAuthen;
    FirebaseFirestore fstore;
    String userId;
    ListView lvUsers;
    UserAdapter userAdapter;
    TextView emptyState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_dependent_view_user_list);


        emptyState = findViewById(R.id.emptyState2);
        lvUsers = findViewById(R.id.userLIst);
        try {
            rootAuthen = FirebaseAuth.getInstance();
            fstore = FirebaseFirestore.getInstance();
            userId = rootAuthen.getCurrentUser().getUid();
        }
        catch(Exception e) {
            Log.d("ERROR", "ERROR" + e);
            Toast.makeText(getApplicationContext(), "Session is invalid, please login", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), main_page.class));
        }

        DocumentReference df = fstore.collection("users").document(userId);

        df.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                try
                {
                    ArrayList<String> userIds = (ArrayList<String>) value.get("users");
                    Log.d("ARRAY", "DATA" + userIds);

                    if(userIds.size() > 0) {
                        emptyState.setVisibility(View.INVISIBLE);
                    }


                    for (String user : userIds) {
                        DocumentReference df = fstore.collection("users").document(user);

                        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot doc = task.getResult();
                                if (doc.exists()) {
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        Base64.Decoder decoder = Base64.getDecoder();
                                        byte[] bytesFN = decoder.decode(doc.get("firstname").toString());
                                        byte[] bytesLN = decoder.decode(doc.get("lastname").toString());
                                        String u  = doc.get("uid").toString();

                                        String f = new String(bytesFN);
                                        String l = new String(bytesLN);

                                        Log.d("F", "firsstname" + f);
                                        Log.d("F", "lname" + l);
                                        if (f.isEmpty() || l.isEmpty()) {
                                            return;
                                        }

                                        User x = new User(u, f, l);
                                        Log.d("X", "X VAL" + x);
                                        users.add(x);
                                    }
                                }

                                userAdapter = new UserAdapter(getApplicationContext(), users);

                                lvUsers.setAdapter(userAdapter);

                                lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    }
                                });
                            }
                        });
                    }


                }
                catch(Exception e) {
                    Log.d("ERROR", "ERROR" + e);
                    Toast.makeText(getApplicationContext(), "Unexpected Error, occured please login again", Toast.LENGTH_LONG);
                    startActivity(new Intent(getApplicationContext(), main_page.class));
                }
            }
        });




        //ArrayAdapter<User> adapter = new ArrayAdapter<>(
                //this, android.R.layout.simple_list_item_1,  users);





    }

    public void UserList_To_Account(View view) {
        Intent intent = new Intent(this, account.class);
        startActivity(intent);
    }

    public void UserList_To_Home(View view) {
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
    }
}
