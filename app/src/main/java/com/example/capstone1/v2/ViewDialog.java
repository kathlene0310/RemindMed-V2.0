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

    public void showDialog(Context activity, String number, String message, String userId, ArrayList<String> ids, List<Notification> notifications){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.v2_notification_card);

        RootRef= FirebaseDatabase.getInstance().getReference();
        TextView text = (TextView) dialog.findViewById(R.id.edit_number);
        text.setText(number);

        TextView msgView = (TextView) dialog.findViewById(R.id.txtMessage);
        msgView.setText(message);


        Button dialogButton = (Button) dialog.findViewById(R.id.btnGotit);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("IDS", "C" + Arrays.toString(ids.toArray()));
                for (int i = 0; i < ids.size(); i++) {
                    Notification notify = notifications.get(i);
                    Log.d("TARGET", "ids.get(i):" + ids.get(i));
                    Log.d("NOTIFICATION", "test" + notify.getRead());
                    notify.setRead(true);
                    Log.d("NOTIFICATION2", "test" + notify.getRead());
                    RootRef.child("Notifications").child(userId).child(ids.get(i)).setValue(notify);
                }
                notifications.clear();
                ids.clear();
                dialog.dismiss();



            }
        });

        dialog.show();

    }
}
