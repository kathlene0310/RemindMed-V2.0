package com.example.capstone1.dependent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.capstone1.home_page;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone1.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class home extends AppCompatActivity {

    FloatingActionButton manage, view, home;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_dependent_home_page);

        manage = findViewById(R.id.manage_profile_button);
        view = findViewById(R.id.view_user_list_button);
        home = findViewById(R.id.home_history);


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


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), home_page.class));
            }
        });
    }



}
