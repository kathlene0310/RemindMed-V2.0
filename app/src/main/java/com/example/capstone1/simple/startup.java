package com.example.capstone1.simple;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone1.R;

public class startup extends AppCompatActivity {
    Button loginBtn;
    int role = 0;
    String[] options = {"Dependent", "User"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.v2_simple_mode_startup);



    }

    public void Startup_To_Home(View view) {
        Intent intent = new Intent(this, shome_page.class);
        startActivity(intent);
    }

    public void Startup_To_Profile(View view) {
        Intent intent = new Intent(this, profile.class);
        startActivity(intent);
    }
}
