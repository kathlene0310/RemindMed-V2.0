package com.example.capstone1.v2;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.capstone1.R;
import com.example.capstone1.home_page;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViewDialog {
    private DatabaseReference RootRef;
    public Dialog dialog;


    public String DialogId;

    public ViewDialog(Context activity, String id) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.v2_notification_card);
        this.DialogId = id;
    }

    public Boolean isShowing() {
        if(dialog != null) {
            return dialog.isShowing();
        }
        else {
            return false;
        }
    }



    public void showDialog(Context activity, String id, String number,Notification notify, String userId){



        RootRef= FirebaseDatabase.getInstance().getReference();
        TextView text = (TextView) dialog.findViewById(R.id.edit_number);
        text.setText(number);

        TextView msgView = (TextView) dialog.findViewById(R.id.txtMessage);
        msgView.setText(notify.getMessage());


        Button dialogButton = (Button) dialog.findViewById(R.id.btnGotit);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify.setRead(true);
                RootRef.child("Notifications").child(userId).child(id).setValue(notify);
                dialog.dismiss();
            }
        });

        try {

            dialog.show();
        }catch(Exception e) {
            Toast.makeText(activity, "One or more notif failed to show message: " + notify.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public String getDialogId() {
        return DialogId;
    }

    public void setDialogId(String dialogId) {
        DialogId = dialogId;
    }

}
