package com.thisatmind.appingpot.appingpot;


import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.thisatmind.appingpot.appingpot.databinding.ActivityMainBinding;
import com.thisatmind.appingpot.appingpot.models.Event;
import com.thisatmind.appingpot.appingpot.pojo.AppCount;
import com.thisatmind.appingpot.appingpot.rest.RestClient;
import com.thisatmind.appingpot.appingpot.rest.model.AppInfo;
import com.thisatmind.appingpot.appingpot.rest.model.EventList;
import com.thisatmind.appingpot.appingpot.rest.service.EventService;
import com.thisatmind.appingpot.appingpot.tracker.Tracker;
import com.thisatmind.appingpot.appingpot.tracker.TrackerDAO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private Realm realm;
    public ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        realm = initRealm();

        final Context context = this;
        Button btn = (Button) findViewById(R.id.saveBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveEventData(new Tracker());
                binding.setAppCount(initAppCount());

                final List<Event> list = new TrackerDAO().getAllEvent(realm);
                List<com.thisatmind.appingpot.appingpot.rest.model.Event> temp =
                        new ArrayList<com.thisatmind.appingpot.appingpot.rest.model.Event>();
                for(int i = 0 ; i < list.size(); i++) {
                    temp.add(list.get(i).getPojo());
                }

                EventList eventList = new EventList("patrick", temp);

                Call<AppInfo> call =
                        RestClient.createService(EventService.class).postEventList(eventList);
                try{

                    call.enqueue(new Callback<AppInfo>(){
                        @Override
                        public void onResponse(Call<AppInfo> call,
                                               Response<AppInfo> response) {
                            Log.d("response","success");
                            Log.d("response",response.body().getModel());
                            Log.d("response",response.body().getName());
                        }

                        @Override
                        public void onFailure(Call<AppInfo> call, Throwable t) {
                            Log.d("response","fail");
                        }
                    });


                }catch(Exception e){
                    e.printStackTrace();
                }
                Log.d("here","at least2");
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
        return new AppCount(maxEvent.getPackageName(), maxEvent.getCount());
    }

    public void saveEventData(Tracker tracker){
        TrackerDAO trackerDAO = new TrackerDAO();
        UsageEvents uEvents = tracker.getUsageEvents(this);
        UsageEvents uEventsTimer = tracker.getUsageEvents(this);

        trackerDAO.saveEventPerHour(tracker.calcEventPerHour(uEvents,
                tracker.getEventStartPoint(this)), this.realm);
        long timer = tracker.calcEventStartPoint(uEventsTimer);
        tracker.setEventStartPoint(this, timer);

//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                RealmQuery<Event> query = realm.where(Event.class);
//                RealmResults<Event> results = query.findAll();
//                for(Event result : results){
//                    Log.d("TEST", result.getPackageName() + " time : " + new Date(result.getDate()).toString()
//                        + " count : " + result.getCount());
//                }
//            }
//        });
    }

    public void saveUsageData(Tracker tracker){
        TrackerDAO trackerDAO = new TrackerDAO();
        UsageEvents uEvents = tracker.getUsageEvents(this);
        UsageEvents uEventsTimer = tracker.getUsageEvents(this);

        trackerDAO.saveUsagePerHour(tracker.calcUsagePerHour(uEvents,
                tracker.getUsageStartPoint(this)), this.realm);
        long timer = tracker.calcUsageStartPoint(uEventsTimer);
        tracker.setUsageStartPoint(this, timer);

    }

    private void getGrant(){
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
    }
}
