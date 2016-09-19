package com.thisatmind.appingpot.appingpot.tracker;

import android.app.Activity;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.thisatmind.appingpot.appingpot.models.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by Patrick on 2016-09-17.
 */
public class Tracker {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");

    public final String TAG = Tracker.class.getSimpleName();

    public List<ApplicationInfo> getInstalledAppList(Activity activity){

        final String TAG = "Installed App List";

        final PackageManager pm = activity.getPackageManager();

        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            Log.d(TAG, "App name : " + pm.getApplicationLabel(packageInfo).toString());
            Log.d(TAG, "Installed package :" + packageInfo.packageName);
            Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
            Log.d(TAG, "ClassName : " + packageInfo.className );
            Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
        }

        return packages;
    }

    public UsageEvents getUsageEvents(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");

        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, -1);
        long startTime = calendar.getTimeInMillis();

        Log.d(TAG, "Range start : " + dateFormat.format(startTime));
        Log.d(TAG, "Range end : " + dateFormat.format(endTime));

        return usm.queryEvents(startTime, endTime);
    }

    public List<UsageStats> getUsageStatsList(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        return usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                0, System.currentTimeMillis());
    }

    public void printfUsageStats(List<UsageStats> usageStatsList){
        for( UsageStats u : usageStatsList ){
            Log.d(TAG, "pkg : " + u.getPackageName() + " / " + "ForegroundTime : "
                + dateFormat.format(u.getTotalTimeInForeground()));
            Log.d(TAG, "Pkg: " + u.getPackageName() + "\t" + "Start Time : "
                    + dateFormat.format(u.getFirstTimeStamp()));
            Log.d(TAG, "Pkg: " + u.getPackageName() + "\t" + "Last Time : "
                    + dateFormat.format(u.getLastTimeUsed()));
        }
    }

    public HashMap<ForegroundEvent, Integer> calcEventPerHour(UsageEvents uEvents, long startPoint){

        UsageEvents.Event e = new UsageEvents.Event();

        HashMap<ForegroundEvent, Integer> map = new HashMap<>();

        boolean isNew = false;

        ForegroundEvent curE;

        String prevEvent = "packageName"; // to remove duplicated event

        while(uEvents.hasNextEvent()){
            uEvents.getNextEvent(e);

            if(!isNew && e.getTimeStamp() > startPoint){
                isNew = true;
            }

            if(isNew){
                if(e.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND
                        && !prevEvent.equals(e.getPackageName())){
                    curE = new ForegroundEvent(e);
                    Integer count = map.get(curE);
                    if(count == null){
                        map.put(curE,1);
                    }else{
                        map.put(curE, count + 1);
                    }
                    prevEvent = e.getPackageName();
                }
            }
        }
//        // test code
//        for(ForegroundEvent key : map.keySet()){
//            Log.d("output", key.getPackageName() + " date : " + dateFormat.format(key.getDate()) + " count : " + map.get(key));
//        }

        return map;
    }

    public HashMap<ForegroundEvent,Long> calcUsagePerHour(UsageEvents uEvents, long startPoint){

        UsageEvents.Event e = new UsageEvents.Event();

        HashMap<ForegroundEvent, Long> map = new HashMap<>();

        boolean isNew = false;

        ForegroundEvent curE;

        long prevTime = 0;

        while(uEvents.hasNextEvent()){
            uEvents.getNextEvent(e);

            if(!isNew && e.getTimeStamp() > startPoint){
                isNew = true;
            }

            if(isNew){
                if(e.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND){
                    prevTime = e.getTimeStamp();
                }else if(e.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND){
                    curE = new ForegroundEvent(e);
                    Long usage = map.get(curE);
                    if (usage == null) {
                        map.put(curE, e.getTimeStamp() - prevTime);
                    } else {
                        map.put(curE, usage + e.getTimeStamp() - prevTime);
                    }
                    prevTime = e.getTimeStamp();
                }
            }
        }
        // test code
        for(ForegroundEvent key : map.keySet()){
            Log.d("output", key.getPackageName() + "time : "+dateFormat.format(key.getDate())
                    +" / usage : " + dateFormat.format(map.get(key)));
        }

        ForegroundEvent startObject = new ForegroundEvent();
        startObject.setPackageName("startPoint");
        startObject.setDate(prevTime);
        map.put(startObject,prevTime);

        return map;
    }

    public long calcUsageStartPoint(UsageEvents uEvents){

        UsageEvents.Event e = new UsageEvents.Event();

        long prevTime = 0;

        while(uEvents.hasNextEvent()) {
            uEvents.getNextEvent(e);
            if (e.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND){
                prevTime = e.getTimeStamp();
            }
        }

        return prevTime;
    }

    public long calcEventStartPoint(UsageEvents uEvents){
        UsageEvents.Event e = new UsageEvents.Event();

        long prevTime = 0;

        while(uEvents.hasNextEvent()){
            uEvents.getNextEvent(e);
            prevTime = e.getTimeStamp();
        }
        return prevTime;
    }

    public static long getCleanMillisecond(long millisecond){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millisecond);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        return cal.getTimeInMillis();
    }

    // @TODO
    // should update better algorithm
    // there is a problem with locale to compare with long
    private boolean isSameDate(long date1, long date2) {

        // If they now are equal then it is the same day.
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.setTimeInMillis(date1);
        cal2.setTimeInMillis(date2);

        return cal1.get(Calendar.HOUR_OF_DAY) == cal2.get(Calendar.HOUR_OF_DAY)
                && cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    public class ForegroundEvent{

        private String packageName;
        private long date;

        public ForegroundEvent(){}

        public ForegroundEvent(UsageEvents.Event e){
            this.packageName = e.getPackageName();
            this.date = getCleanMillisecond(e.getTimeStamp());
        };

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
        }

        @Override
        public int hashCode() {
            int result = packageName.hashCode();
            result = 31 * result + (int)date;
            return result;
        }

        @Override
        public boolean equals(Object obj) {

            if(obj instanceof ForegroundEvent){

                ForegroundEvent e = (ForegroundEvent)obj;

                return e.getPackageName().equals(this.packageName)
                        && isSameDate(e.getDate(), this.date);
            }

            return false;
        }
    }

    public long getEventStartPoint(Context context){
        return context.getSharedPreferences("Tracker", Context.MODE_PRIVATE)
                .getLong("eventStartPoint", 0);
    }
    public void setEventStartPoint(Context context, long startPoint){
        SharedPreferences sharedPreference
                = context.getSharedPreferences("Tracker", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putLong("eventStartPoint", startPoint);
        editor.apply();
    }
    public long getUsageStartPoint(Context context){
        return context.getSharedPreferences("Tracker", Context.MODE_PRIVATE)
                .getLong("usageStartPoint", 0);
    }
    public void setUsageStartPoint(Context context, long startPoint){
        SharedPreferences sharedPreference
                = context.getSharedPreferences("Tracker", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putLong("usageStartPoint", startPoint);
        editor.apply();
    }

}
