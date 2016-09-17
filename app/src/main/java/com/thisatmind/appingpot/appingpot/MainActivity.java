package com.thisatmind.appingpot.appingpot;


import android.databinding.DataBindingUtil;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.thisatmind.appingpot.appingpot.databinding.ActivityMainBinding;
import com.thisatmind.appingpot.appingpot.models.Event;
import com.thisatmind.appingpot.appingpot.pojo.AppCount;
import com.thisatmind.appingpot.appingpot.tracker.Tracker;

import java.util.Calendar;
import java.util.HashMap;

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

        Button btn = (Button) findViewById(R.id.saveBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                binding.setAppCount(initAppCount());
            }
        });

//        new Tracker().calcEventPerHour(new Tracker().getUsageEvents(this), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        AppCount initData = initAppCount();
        if(initData == null) {
            initData = new AppCount("defaultPacakgeName", 0);
        }
        binding.setAppCount(initData);
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
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try{
                    Tracker tracker = new Tracker();
                    HashMap<Tracker.ForegroundEvent, Integer> eventMap =
                            tracker.calcEventPerHour(tracker.getUsageEvents(MainActivity.this),
                                tracker.getStartPoint(MainActivity.this));

                    for(Tracker.ForegroundEvent e : eventMap.keySet()){

                        RealmQuery<Event> query = realm.where(Event.class);
                        query.equalTo("packageName", e.getPackageName())
                                .equalTo("date", e.getDate());
                        RealmResults<Event> result = query.findAll();

                        Event event;
                        if(result.size() == 0){
                            event = realm.createObject(Event.class);
                            event.setPacakageName(e.getPackageName());
                            event.setDate(e.getDate());
                            event.setCount(eventMap.get(e));
                        }else{
                            event = result.get(0);
                            event.setCount(event.getCount() + eventMap.get(e));
                        }
                    }

//                    HashMap<Tracker.ForegroundEvent, Long> usageMap =
//                            tracker.calcUsagePerHour(tracker.getUsageEvents(MainActivity.this),
//                                tracker.getStartPoint(MainActivity.this));



                }catch(Exception e){
                    Log.d("TEST", "exception here :");
                }
            }
        });
    }

}
