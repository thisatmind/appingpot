package com.thisatmind.appingpot.appingpot.tracker;

import android.util.Log;

import com.thisatmind.appingpot.appingpot.models.Event;

import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


/**
 * Created by patrick on 2016-09-19.
 */
public class TrackerDAO {

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
                        keyE.setPackageName(event.getPacakageName());
                        keyE.setDate(Tracker.getCleanMillisecond(event.getDate()));
                        event.setCount(event.getCount() + map.get(keyE));
                        map.remove(keyE);
                    }

                    for (Tracker.ForegroundEvent restE : map.keySet()) {
                        Event updateE = realm.createObject(Event.class);
                        updateE.setKey(updateE.genKey(restE.getPackageName(),restE.getDate()));
                        updateE.setPacakageName(restE.getPackageName());
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

}
