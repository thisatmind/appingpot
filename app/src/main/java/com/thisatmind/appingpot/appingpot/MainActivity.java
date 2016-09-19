package com.thisatmind.appingpot.appingpot;


import android.app.usage.UsageEvents;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.thisatmind.appingpot.appingpot.databinding.ActivityMainBinding;
import com.thisatmind.appingpot.appingpot.models.Event;
import com.thisatmind.appingpot.appingpot.pojo.AppCount;
import com.thisatmind.appingpot.appingpot.tracker.Tracker;
import com.thisatmind.appingpot.appingpot.tracker.TrackerDAO;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;


public class MainActivity extends AppCompatActivity {

    private Realm realm;
    public ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        this.realm = initRealm();

        getGrant();

        Button btn = (Button) findViewById(R.id.saveBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                binding.setAppCount(initAppCount());
            }
        });

        // testcode
//        new Tracker().calcEventPerHour(new Tracker().getUsageEvents(this),0);
//        new Tracker().calcUsagePerHour(new Tracker().getUsageEvents(this),0);
//        new Tracker().calcEventPerHour(new Tracker().getUsageEvents(this), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        AppCount initData = initAppCount();
//        if(initData == null) {
//            initData = new AppCount("defaultPacakgeName", 0);
//        }
//        binding.setAppCount(initData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private Realm initRealm(){
        RealmConfiguration config =
                new RealmConfiguration.Builder(this)
                        .deleteRealmIfMigrationNeeded()
                        .build();
        Realm.setDefaultConfiguration(config);
        return Realm.getDefaultInstance();
    }

    public AppCount initAppCount(){
        RealmQuery<Event> query = realm.where(Event.class);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        query.greaterThan("date", cal.getTimeInMillis());
        RealmResults<Event> result = query.findAll();

        if(result.size() == 0 ) return null;
        result.sort("count", Sort.DESCENDING);
        Event maxEvent = result.get(0);
        return new AppCount(maxEvent.getPacakageName(), maxEvent.getCount());
    }

    public void saveData(){
        Tracker tracker = new Tracker();
        TrackerDAO trackerDAO = new TrackerDAO();
        UsageEvents uEvents = tracker.getUsageEvents(this);
        UsageEvents uEventsTimer = tracker.getUsageEvents(this);

        trackerDAO.saveEventPerHour(tracker.calcEventPerHour(uEvents,
                tracker.getEventStartPoint(this)), this.realm);
        long timer = tracker.calcEventStartPoint(uEventsTimer);
        tracker.setEventStartPoint(this, timer);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmQuery<Event> query = realm.where(Event.class);
                RealmResults<Event> results = query.findAll();
//                for(Event result : results){
//                    Log.d("TEST", result.getPacakageName() + " time : " + new Date(result.getDate()).toString()
//                        + " count : " + result.getCount());
//                }
            }
        });

    }

    private void getGrant(){
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
    }
}
