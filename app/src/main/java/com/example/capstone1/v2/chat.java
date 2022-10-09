package com.example.capstone1.v2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone1.R;
import com.example.capstone1.home_page;
import com.example.capstone1.today;
import com.example.capstone1.history_page;
import com.example.capstone1.user_information;
public class chat extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_chat);




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

    public void Chat_To_Profile(View view) {
        Intent intent = new Intent(chat.this, user_information.class);
        startActivity(intent);
    }

}
