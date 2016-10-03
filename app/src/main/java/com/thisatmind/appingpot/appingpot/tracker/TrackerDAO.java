package com.thisatmind.appingpot.appingpot.tracker;

import android.util.Log;

import com.thisatmind.appingpot.appingpot.models.Event;
import com.thisatmind.appingpot.appingpot.models.Usage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


/**
 * Created by patrick on 2016-09-19.
 */
public class TrackerDAO {

    public List<Event> getAllEvent(Realm realm){
        final List<Event> list = new ArrayList<>();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmQuery<Event> query = realm.where(Event.class);
                RealmResults<Event> results = query.findAll();
                for(Event result : results){
                    list.add(result);
                    Log.d("TEST", result.getPackageName() + " time : " + new Date(result.getDate()).toString()
                            + " count : " + Integer.toString(result.getCount()));
                }
            }
        });
        return list;
    }

    public void saveEventPerHour(HashMap<Tracker.ForegroundEvent, Integer> eventMap, Realm realm){

        final HashMap<Tracker.ForegroundEvent, Integer> map = eventMap;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    RealmQuery<Event> query = realm.where(Event.class);
                    boolean isFirst = true;
                    for (Tracker.ForegroundEvent e : map.keySet()) {
                        if (!isFirst) query.or();
                        query.equalTo("packageName", e.getPackageName())
                                .equalTo("date", e.getDate());
                        isFirst = false;
                    }
                    RealmResults<Event> result = query.findAll();

                    Tracker.ForegroundEvent keyE = new Tracker().new ForegroundEvent();

                    for (Event event : result) {
                        keyE.setPackageName(event.getPackageName());
                        keyE.setDate(Tracker.getCleanMillisecond(event.getDate()));
                        event.setCount(event.getCount() + map.get(keyE));
                        map.remove(keyE);
                    }

                    for (Tracker.ForegroundEvent restE : map.keySet()) {
                        Event updateE = realm.createObject(Event.class);
                        updateE.setKey(updateE.genKey(restE.getPackageName(),restE.getDate()));
                        updateE.setPackageName(restE.getPackageName());
                        updateE.setDate(restE.getDate());
                        updateE.setCount(map.get(restE));
                    }
                } catch (Exception e) {
                    Log.d("TEST", e.toString());
                    Log.d("TEST", "exception here : Tracker DAO");
                }
            }
        });
    }

    public void saveUsagePerHour(HashMap<Tracker.ForegroundEvent, Long> eventMap, Realm realm){

        final HashMap<Tracker.ForegroundEvent, Long> map = eventMap;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
            try {
                RealmQuery<Usage> query = realm.where(Usage.class);
                boolean isFirst = true;
                for (Tracker.ForegroundEvent e : map.keySet()) {
                    if (!isFirst) query.or();
                    query.equalTo("packageName", e.getPackageName())
                            .equalTo("date", e.getDate());
                    isFirst = false;
                }
                RealmResults<Usage> result = query.findAll();

                Tracker.ForegroundEvent keyE = new Tracker().new ForegroundEvent();

                for (Usage usage : result) {
                    keyE.setPackageName(usage.getPackageName());
                    keyE.setDate(Tracker.getCleanMillisecond(usage.getDate()));
                    usage.setUsage(usage.getUsage() + map.get(keyE));
                    map.remove(keyE);
                }

                for (Tracker.ForegroundEvent restE : map.keySet()) {
                    Usage updateE = realm.createObject(Usage.class);
                    updateE.setKey(updateE.genKey(restE.getPackageName(),restE.getDate()));
                    updateE.setPackageName(restE.getPackageName());
                    updateE.setDate(restE.getDate());
                    updateE.setUsage(map.get(restE));
                }
            } catch (Exception e) {
                Log.d("TEST", e.toString());
                Log.d("TEST", "exception here : Tracker DAO");
            }
            }
        });
    }
}
