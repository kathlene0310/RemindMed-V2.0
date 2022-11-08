package com.example.capstone1.dependent;

import static com.example.capstone1.home_page.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
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
import com.example.capstone1.main_page;
import com.example.capstone1.today;
import com.example.capstone1.history_page;
import com.example.capstone1.user_information;
import com.example.capstone1.v2.ChatActivity;
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

public class d_chat extends AppCompatActivity {
    ArrayList<User> users = new ArrayList<User>();
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    FirebaseAuth rootAuthen;
    String userId;
    ListView lvUsers;
    UserAdapter userAdapter;
    FloatingActionButton profileBtn;
    long accounttype ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_dependent_chat);


        rootAuthen = FirebaseAuth.getInstance();
        userId = rootAuthen.getCurrentUser().getUid();
        lvUsers = findViewById(R.id.userLIst);



        DocumentReference df = fstore.collection("users").document(userId);

        df.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@com.google.firebase.database.annotations.Nullable DocumentSnapshot value, @com.google.firebase.database.annotations.Nullable FirebaseFirestoreException error) {
                try
                {
                    ArrayList<String> userIds = (ArrayList<String>) value.get("users");
                    Log.d("ARRAY", "DATA" + userIds);




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
                                        String u = doc.get("uid").toString();
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
                                        TextView uid = (TextView) view.findViewById(R.id.txtUID);
                                        TextView tName = (TextView) view.findViewById(R.id.txtFirstname);
                                        String userid = uid.getText().toString();
                                        String name = tName.getText().toString();
                                        final String[] image = {"default_image"};

                                        Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
                                        chatIntent.putExtra("visit_user_id", userid);
                                        chatIntent.putExtra("visit_user_name", name);
                                        chatIntent.putExtra("visit_image", image[0]);
                                        startActivity(chatIntent);
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



    }
    public void Chat_To_Home(View view) {
        Intent intent = new Intent(d_chat.this, home.class);
        startActivity(intent);
    }

    public void Chat_To_Today(View view) {
        Intent intent = new Intent(d_chat.this, today.class);
        startActivity(intent);
    }

    public void Chat_To_History(View view) {
        Intent intent = new Intent(d_chat.this, history_page.class);
        startActivity(intent);
    }

    public void Chat_To_Chat(View view) {
        Intent intent = new Intent(d_chat.this, d_chat.class);
        startActivity(intent);
    }

    public void Chat_To_Profile(View view) {
        Intent intent = new Intent(d_chat.this, account.class);
        startActivity(intent);
    }

}
