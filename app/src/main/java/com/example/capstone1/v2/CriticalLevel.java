package com.example.capstone1.v2;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.capstone1.home_page;
import com.example.capstone1.medication_info;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CriticalLevel {
    public static DatabaseReference RootRef;
    public static FirebaseFirestore fstore;
    public static FirebaseAuth rootAuthen;
    public static String userLog;
    public static List<String> notifMessageList;

    public static void getAllNotif() {
        notifMessageList = new ArrayList<String>();
        rootAuthen = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userLog = rootAuthen.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        if (rootAuthen != null && fstore != null && userLog != null && RootRef != null){
            com.google.firebase.database.Query userRef = RootRef.child("Notifications").child(rootAuthen.getCurrentUser().getUid());

            userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {

                        Log.d("firebase", "Error getting data", task.getException());




                    }
                    else {
                        Log.d("firebase", String.valueOf(task.getResult()));
                        int count = 0;
                        for(DataSnapshot ds : task.getResult().getChildren()) {
                            String id = ds.getKey();
                            Notification notify = ds.getValue(Notification.class);
                            notifMessageList.add(notify.getMessage());
                            count++;

                        }


                        Log.d("MESSAGE LIST", "LIST: " + notifMessageList);
                        readCriticalLevel();

                    }

                }
            });

        }

    }
    public static void readCriticalLevel() {

        try {
            if (rootAuthen != null && fstore != null && userLog != null){
                fstore.collection("users").document(userLog).collection("New Medications").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if(error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }

                        Log.d("ASD", "CALLED READ CRITICAL LEVEL");

                        List<medication_info> medications = new ArrayList<>();

                        for(QueryDocumentSnapshot doc : value) {
                            Log.d(home_page.TAG, doc.getId() + " => " + doc.getData());
                            medication_info obj = doc.toObject(medication_info.class);


                            if(obj.getMedicineTypeName() != null && obj.getInventoryMeds() != null && obj.getMedication() != null) {

                                Boolean critical = false;
                                String type = obj.getMedicineTypeName();
                                String medicineName = obj.getMedication();
                                String inventory = obj.getInventoryMeds();
                                Log.d("TYPE", obj.getMedicineTypeName());
                                if(type.equals("ml")) {

                                    critical = isCritical(obj.getMedicineTypeName(), Integer.parseInt(obj.getInventoryMeds()), "ml", 20);
                                    Log.d("ISCRITICAL", "O:" + critical);
                                    if(critical == true) {
                                        createNotif(medicineName, inventory,type);
                                    }
                                    Log.d("Critical Level", "ML");
                                }
                                else if(type.equals("Pills")) {
                                    critical = isCritical(obj.getMedicineTypeName(), Integer.parseInt(obj.getInventoryMeds()), "Pills", 7);
                                    Log.d("ISCRITICAL", "O:" + critical);
                                    if(critical == true) {
                                        createNotif(medicineName, inventory, type);
                                    }
                                    Log.d("Critical Level", "Pills");
                                }
                                else if(type.equals("Capsule")) {
                                    critical = isCritical(obj.getMedicineTypeName(), Integer.parseInt(obj.getInventoryMeds()), "Capsule", 7);
                                    Log.d("ISCRITICAL", "O:" + critical);
                                    if(critical == true) {
                                        createNotif(medicineName, inventory, type);
                                    }
                                    Log.d("Critical Level", "Capsule");
                                }
                                else if(type.equals("Tablet")) {
                                    critical = isCritical(obj.getMedicineTypeName(), Integer.parseInt(obj.getInventoryMeds()), "Tablet", 7);
                                    Log.d("ISCRITICAL", "O:" + critical);
                                    if(critical == true) {
                                        createNotif(medicineName, inventory, type);
                                    }
                                    Log.d("Critical Level", "Tablets");
                                }

                            }

                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.d("CriticalLevel", "ERROR" + e);
        }

    }

    public static Boolean isCritical(String orig, int inventory, String medtype, int critical) {
        if(orig.equals(medtype)) {
            if(inventory <= critical ) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public static void createNotif(String medicineName, String inventory, String typeName) {

        try {

            Log.d("MESSAGE LIST", "LIST: " + notifMessageList);
            Notification notify = new Notification(rootAuthen.getCurrentUser().getUid(), userLog, "Critical", medicineName, null, null, null, null, 0, null, 0, null, typeName, inventory);
            notify.setMessage(notify.buildMessage2());
            Log.d("MESSAGE TARGET", "O: " + notify.buildMessage2());
            if (!notifMessageList.contains(notify.buildMessage2())) {
                DatabaseReference notifKeyRef = RootRef.child("Notifications").child(rootAuthen.getCurrentUser().getUid()).push();
                final String notifyPushID = notifKeyRef.getKey();
                //RootRef.child("Notifications").child(userId).child(notifyPushID).setValue("");
                RootRef.child("Notifications").child(rootAuthen.getCurrentUser().getUid()).child(notifyPushID).setValue(notify);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }

    }


}
