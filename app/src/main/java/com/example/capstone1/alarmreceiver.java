package com.example.capstone1;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.capstone1.v2.SharedPref;

import java.util.Timer;
import java.util.TimerTask;

public class alarmreceiver extends BroadcastReceiver {
    static Ringtone ringtone;
    static Timer mTimer;
    static MediaPlayer media;
    SharedPref sf;
    @Override
    public void onReceive(Context context, Intent intent) {
        sf = new SharedPref(context);
        Rumble.init(context);



        try {


            String alarm = sf.getAlarmSound();

            if(alarm.equals("Beep")) {
                media = MediaPlayer.create(context, R.raw.beep);
                media.setAudioStreamType(AudioManager.STREAM_MUSIC);
                media.setLooping(true);
                media.start();

            } else if(alarm.equals("Bell")) {
                media = MediaPlayer.create(context, R.raw.bell);
                media.setAudioStreamType(AudioManager.STREAM_MUSIC);
                media.setLooping(true);
                media.start();

            } else if(alarm.equals("Clock")) {
                media = MediaPlayer.create(context, R.raw.clock);
                media.setAudioStreamType(AudioManager.STREAM_MUSIC);
                media.setLooping(true);
                media.start();

            }else if(alarm.equals("Simple")) {
                media = MediaPlayer.create(context, R.raw.simple);
                media.setAudioStreamType(AudioManager.STREAM_MUSIC);
                media.setLooping(true);
                media.start();

            }else if(alarm.equals("Wave")) {
                media = MediaPlayer.create(context, R.raw.wave);
                media.setAudioStreamType(AudioManager.STREAM_MUSIC);
                media.setLooping(true);
                media.start();
            } else {
                media = MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI);
                media.setAudioStreamType(AudioManager.STREAM_MUSIC);
                media.setLooping(true);
                media.start();
            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }


        Intent i = new Intent(context, alarm_notification.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i , 0 );

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        NotificationCompat.Builder builder =  new NotificationCompat.Builder(context, "MyApp")
                .setSmallIcon(R.drawable.logoicon)
                .setContentTitle("Take your Medication")
                .setContentText("Its time to take your medication")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123, builder.build());


        //ringtone = RingtoneManager.getRingtone(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
        //ringtone.play();

        if(sf.getAlarmVibration().equals("Heartbeat")) {
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    Rumble.once(100);
                }
            }, 1000*1, 1000*1);
        }
        else if(sf.getAlarmVibration().equals("Ticktock")
        ){
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    Rumble.once(300);
                }
            }, 1300*1, 1300*1);

        }
        else if(sf.getAlarmVibration().equals("Zig-zig-zig")){
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    Rumble.once(400);
                }
            }, 500*1, 500*1);
        }
        else {
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    Rumble.once(400);
                }
            }, 1000*1, 1000*1);
        }
              /*
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                if (!ringtone.isPlaying()) {
                    ringtone.play();
                }


            }
        }, 1000*1, 1000*1);
   */

        context.startActivity(i);



    }



}

