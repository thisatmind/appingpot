package com.thisatmind.appingpot.appingpot.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by patrick on 2016-09-21.
 */
public class OnBootService extends Service{

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        Toast.makeText(this,"onStartCommand", Toast.LENGTH_LONG).show();
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

}
