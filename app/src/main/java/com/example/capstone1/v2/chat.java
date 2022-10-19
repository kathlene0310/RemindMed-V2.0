package com.example.capstone1.v2;

import static com.example.capstone1.home_page.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone1.R;
import com.example.capstone1.guestLogout;
import com.example.capstone1.health_measurements;
import com.example.capstone1.home_page;
import com.example.capstone1.today;
import com.example.capstone1.history_page;
import com.example.capstone1.user_information;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class chat extends AppCompatActivity {
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    FirebaseAuth rootAuthen;
    String userId;
    FloatingActionButton profileBtn;
    long accounttype ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_chat);


        rootAuthen = FirebaseAuth.getInstance();
        userId = rootAuthen.getCurrentUser().getUid();
        profileBtn = findViewById(R.id.profile_chat);

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
