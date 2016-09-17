package com.thisatmind.appingpot.appingpot;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thisatmind.appingpot.appingpot.databinding.ActivityMainBinding;
import com.thisatmind.appingpot.appingpot.models.Event;
import com.thisatmind.appingpot.appingpot.pojo.AppCount;
import com.thisatmind.appingpot.appingpot.tracker.Tracker;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class MainActivity extends AppCompatActivity {

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        final ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        RealmConfiguration config =
                new RealmConfiguration.Builder(this)
                        .deleteRealmIfMigrationNeeded()
                        .build();
        Realm.setDefaultConfiguration(config);
        this.realm = Realm.getDefaultInstance();

        AppCount initData = init();
        if(initData == null) {
            Log.d("TEST","no data");
            initData = new AppCount("com.kakao.talk", 300);
        }
        binding.setAppCount(initData);


        Button btn = (Button) findViewById(R.id.saveBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                binding.setAppCount(new AppCount("com.facebook", 10000));
            }
        });
    }

    public AppCount init(){

        // Build the query looking at all users:
        RealmQuery<Event> query = realm.where(Event.class);
        query.equalTo("packageName", "com.facebook");
        RealmResults<Event> result = query.findAll();
        Log.d("TEST size", Integer.toString(result.size()));
        if(result.size() == 0 ) return null;
        Event facebook = result.get(0);
        return new AppCount(facebook.getPacakageName(),facebook.getCount());
    }

    public void saveData(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try{
                    Event event = realm.createObject(Event.class);
                    event.setPacakageName("com.facebook");
                    event.setDate(new Date());
                    event.setCount(10000);
                }catch(Exception e){
                    Log.d("TEST", "exception here :)");
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
