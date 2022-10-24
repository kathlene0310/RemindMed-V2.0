package com.example.capstone1.v2;

import static com.example.capstone1.home_page.TAG;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.speech.tts.TextToSpeech;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone1.R;
import com.example.capstone1.guestLogout;
import com.example.capstone1.user_information;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Locale;

public class tts extends AppCompatActivity {
    Button test, save, reset;
    EditText speed, pitch;
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    FirebaseAuth rootAuthen;
    String userId;
    FloatingActionButton profileBtn;
    SharedPref sf;
    TextToSpeech textToSpeech;
    Spinner voice;
    ArrayList<String> listVoice;
    String selectedVoice;
    long accounttype ;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_account_settings_tts_config);


        reset = findViewById(R.id.btnReset);
        voice = findViewById(R.id.pronunciation_spinner);
        test = findViewById(R.id.btnTest);
        save = findViewById(R.id.btnSave);
        speed = findViewById(R.id.eSpeed);
        pitch = findViewById(R.id.ePitch);
        sf = new SharedPref(getApplicationContext());



        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS)
                {
                    int result = textToSpeech.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                    {
                        Log.e("TTS", "Language not supported");
                    }
                    else
                    {

                        listVoice = new ArrayList<String>();
                        test.setEnabled(true);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            for (Voice tmpVoice : textToSpeech.getVoices()) {

                                listVoice.add(tmpVoice.getName().toString());
                            }
                            Log.d("TTS", "VOICES:" + listVoice);

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, listVoice);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            voice.setAdapter(adapter);

                            voice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    String value = parent.getItemAtPosition(position).toString();

                                    Log.d("D", value);

                                    selectedVoice = value;

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            voice.setSelection(35);
                        }

                    }

                }
                else
                {
                    Log.e("TTS", "Iniitialization failed");
                }
            }
        });

        save = findViewById(R.id.btnSave);
        test = findViewById(R.id.btnTest);



        speed.setText(sf.getSpeed().toString());
        pitch.setText(sf.getPitch().toString());


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sf.setPitch(1.0f);
                sf.setSpeed(1.0f);
                voice.setSelection(35);
                pitch.setText("1.0");
                speed.setText("1.0");

            }
        });



        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Log.d("TTS", "VOICES" + textToSpeech.getVoices());
                    Voice va = new Voice("ja-jp-x-htm-network", new Locale("en", "US"), 400, 200, true, null);
                    textToSpeech.setVoice(va);
                }


                    if(!selectedVoice.isEmpty()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            for (Voice tmpVoice : textToSpeech.getVoices()) {
                                if (tmpVoice.getName().equals(selectedVoice)) {
                                    textToSpeech.setVoice(tmpVoice);
                                    break;
                                }
                            }
                        }

                    }

                    textToSpeech.setPitch(sf.getPitch());
                    textToSpeech.setSpeechRate(sf.getSpeed());
                    textToSpeech.speak("This is a test, Hi my name is baymax", TextToSpeech.QUEUE_FLUSH,  null);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sf.setPitch(Float.parseFloat(pitch.getText().toString()));
                sf.setSpeed(Float.parseFloat(speed.getText().toString()));
                sf.setVoice(selectedVoice);
                Toast.makeText(getApplicationContext(), "Saved Configuration", Toast.LENGTH_LONG).show();
            }
        });








    }

    public void Back(View view) {
        finish();
    }
}
