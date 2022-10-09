package com.example.capstone1.dependent;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone1.R;

public class profile extends AppCompatActivity {
    Button loginBtn;
    int role = 0;
    String[] options = {"Dependent", "User"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_dependent_manage_profile_page);







    }
}
