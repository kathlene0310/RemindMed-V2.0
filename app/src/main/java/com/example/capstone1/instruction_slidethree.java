package com.example.capstone1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.example.capstone1.dependent.home;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class instruction_slidethree extends AppCompatActivity {
    private float x1, x2, y1, y2;
    private ImageView button;
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    FirebaseAuth rootAuthen;
    String userId;
    int role;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction_slidethree);


        rootAuthen = FirebaseAuth.getInstance();
        userId = rootAuthen.getCurrentUser().getUid();

        DocumentReference documentReference = fstore.collection("users").document(userId);
        Log.d("TAG","UIDuser: "+ userId);


        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("ERR", "listen:error", error);

                }
                try {
                    int r = Integer.parseInt(value.get("role").toString());
                    role = r;
                    Log.d("ROLE", "YOUR ROLE:" + role);
                }
                catch (Exception e) {

                }
            }
        });

    }

    private void openhome_page() {
        Intent intent = new Intent(this, home_page.class);
        startActivity(intent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent)
    {
        switch(motionEvent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = motionEvent.getX();
                y1 = motionEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = motionEvent.getX();
                y2 = motionEvent.getY();
                if (x1 < x2)
                {
                    Intent intent = new Intent(instruction_slidethree.this, instruction_slidetwo.class);
                    startActivity(intent);
                }
                else if (x1 > x2)
                {

                    if(role == 1) {
                        startActivity(new Intent(getApplicationContext(), home.class));
                    }
                    else {
                        Intent intent = new Intent(instruction_slidethree.this, home_page.class);
                        startActivity(intent);
                    }
                }
                break;
        }
        return false;
    }
}