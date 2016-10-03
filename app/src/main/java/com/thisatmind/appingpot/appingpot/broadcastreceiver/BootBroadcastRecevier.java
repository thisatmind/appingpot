package com.thisatmind.appingpot.appingpot.broadcastreceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.thisatmind.appingpot.appingpot.service.OnBootService;

/**
 * Created by patrick on 2016-09-21.
 */
public class BootBroadcastRecevier extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent){

        Toast.makeText(context,"At LEAST HERE",Toast.LENGTH_LONG).show();
//        if (intent.getAction().equals(ACTION)) {
            Log.d("HERE", "BOOT COMPLETE");
            Toast.makeText(context,"BOOT COMPLETE",Toast.LENGTH_LONG).show();

            Intent serviceIntent = new Intent(context, OnBootService.class);
            PendingIntent pIntent = PendingIntent.getService(context, 0, serviceIntent, 0);
            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                    3000, pIntent);
    }


}
