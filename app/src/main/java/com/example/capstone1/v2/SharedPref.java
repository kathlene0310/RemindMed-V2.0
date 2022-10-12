package com.example.capstone1.v2;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

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


}
