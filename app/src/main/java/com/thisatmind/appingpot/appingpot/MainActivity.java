package com.thisatmind.appingpot.appingpot;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.thisatmind.appingpot.appingpot.tracker.Tracker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Tracker.getUsageStatsList(this) == null){
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }


//        Tracker.getUsageEvents(this);
//        Tracker.printfUsageStats(Tracker.getUsageStatsList(this));
//        new Tracker().calcEvent(Tracker.getUsageEvents(this), 0);

        new Tracker().calcUsage(Tracker.getUsageEvents(this), 0);
    }




}
