package com.example.capstone1.simple;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone1.R;
import com.example.capstone1.home_page;
import com.example.capstone1.main_page;
import com.example.capstone1.user_information;
import com.example.capstone1.v2.SharedPref;
import com.example.capstone1.v2.edit_dependent;
import com.google.firebase.auth.FirebaseAuth;

public class shome_page extends AppCompatActivity {
    Button loginBtn;
    Spinner add, view, add_dependent;
    Button editDependent;
    SharedPref sf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_simple_mode_home_page);
        try {
            sf = new SharedPref(getApplicationContext());
            if(sf.getSimpleMode() == false) {
                startActivity(new Intent(getApplicationContext(), home_page.class));
            }


        }catch (Exception e) {
            Log.d("Except", "EXCEPTION" + e);
            startActivity(new Intent(getApplicationContext(), main_page.class));
        }

        editDependent = findViewById(R.id.frequency_set_later_bs11);
        add = findViewById(R.id.frequency_set_later_bs);
        view = findViewById(R.id.frequency_set_later_bs3);
        add_dependent = findViewById(R.id.frequency_set_later_bs4);
        add.setOnTouchListener(Spinner_OnTouch);
        add.setOnKeyListener(Spinner_OnKey);
        view.setOnTouchListener(Spinner_OnTouch2);
        view.setOnKeyListener(Spinner_OnKey2);
        add_dependent.setOnTouchListener(Spinner_OnTouch3);
        add_dependent.setOnKeyListener(Spinner_OnKey3);



        editDependent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(shome_page.this, edit_dependent.class));
            }
        });
    }

    public void SHome_To_Home(View view) {
        Intent intent = new Intent(this, shome_page.class);
        startActivity(intent);
    }

    public void SHome_To_Profile(View view) {
        Intent intent = new Intent(this, profile.class);
        startActivity(intent);
    }

    private View.OnTouchListener Spinner_OnTouch = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                startActivity(new Intent(getApplicationContext(), add_medication.class));
            }
            return true;
        }
    };



    private View.OnKeyListener Spinner_OnKey = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                startActivity(new Intent(getApplicationContext(), add_medication.class));
                return true;
            } else {
                return false;
            }
        }
    };

    private View.OnTouchListener Spinner_OnTouch2 = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                startActivity(new Intent(getApplicationContext(), view_medication.class));
            }
            return true;
        }
    };

    private View.OnKeyListener Spinner_OnKey2 = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                startActivity(new Intent(getApplicationContext(), view_medication.class));
                return true;
            } else {
                return false;
            }
        }
    };

    private View.OnTouchListener Spinner_OnTouch3 = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                startActivity(new Intent(getApplicationContext(), add_dependent.class));
            }
            return true;
        }
    };

    private View.OnKeyListener Spinner_OnKey3 = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                startActivity(new Intent(getApplicationContext(), add_dependent.class));
                return true;
            } else {
                return false;
            }
        }
    };



}
