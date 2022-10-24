package com.example.capstone1.dependent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.example.capstone1.home_page;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone1.R;
import com.example.capstone1.main_page;
import com.example.capstone1.simple.shome_page;
import com.example.capstone1.v2.SharedPref;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class home extends AppCompatActivity {

    FloatingActionButton manage, view, home, chat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_dependent_home_page);




        manage = findViewById(R.id.manage_profile_button);
        view = findViewById(R.id.view_user_list_button);
        home = findViewById(R.id.home_history);
        chat = findViewById(R.id.chat_button);


        manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), profile.class));
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), user_list.class));
            }
        });


        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), d_chat.class));
            }
        });



    }

    public void Home_To_Account(View view) {
        Intent intent = new Intent(this, account.class);
        startActivity(intent);
    }

    public void Home_To_DependentHome(View view) {
        Log.d("TAG", "CALLED");
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
    }

}
