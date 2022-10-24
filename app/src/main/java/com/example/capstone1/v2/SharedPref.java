package com.example.capstone1.v2;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.Voice;

public class SharedPref {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;


    public SharedPref(Context ctx) {
        // Storing data into SharedPreferences
        sharedPreferences = ctx.getSharedPreferences("SimpleMode", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();
        setMyEdit(myEdit);
        setSharedPreferences(sharedPreferences);
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public SharedPreferences.Editor getMyEdit() {
        return myEdit;
    }

    public void setMyEdit(SharedPreferences.Editor myEdit) {
        this.myEdit = myEdit;
    }

    public void setSimpleMode(Boolean mode) {
        this.myEdit.putBoolean("simplemode", mode);
        this.myEdit.commit();
    }

    public Boolean getSimpleMode() {
        return this.sharedPreferences.getBoolean("simplemode", false);
    }

    public void setPitch(Float pitch) {
        this.myEdit.putFloat("pitch", pitch);
        this.myEdit.commit();
    }

    public void setSpeed(Float speechRate) {
        this.myEdit.putFloat("speed", speechRate);
        this.myEdit.commit();
    }

    public void setVoice(String voice_name) {
        this.myEdit.putString("voice", voice_name);
        this.myEdit.commit();
    }

    public String getVoice() {
        return this.sharedPreferences.getString("voice", "en");
    }
    public Float getPitch() {
        return this.sharedPreferences.getFloat("pitch", 1);
    }

    public Float getSpeed() {
        return this.sharedPreferences.getFloat("speed", 1);
    }
    /*
    public void setVoice(Voice voice) {
        this.myEdit.put("voice", voice);
        this.myEdit.commit();
    }

     */

}
