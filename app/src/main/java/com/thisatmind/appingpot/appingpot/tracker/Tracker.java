package com.thisatmind.appingpot.appingpot.tracker;

import android.app.Activity;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Patrick on 2016-09-17.
 */
public class Tracker {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");

    public static final String TAG = Tracker.class.getSimpleName();

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

    public static UsageEvents getUsageEvents(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, -1);
        long startTime = calendar.getTimeInMillis();

        Log.d(TAG, "Range start : " + dateFormat.format(startTime));
        Log.d(TAG, "Range end : " + dateFormat.format(endTime));

        UsageEvents uEvents = usm.queryEvents(startTime, endTime);
        return uEvents;

    }

    public static List<UsageStats> getUsageStatsList(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                0, System.currentTimeMillis());
        return usageStatsList;
    }

    public static void printfUsageStats(List<UsageStats> usageStatsList){
        for( UsageStats u : usageStatsList ){
            Log.d(TAG, "pkg : " + u.getPackageName() + " / " + "ForegroundTime : "
                + dateFormat.format(u.getTotalTimeInForeground()));
            Log.d(TAG, "Pkg: " + u.getPackageName() + "\t" + "Start Time : "
                    + dateFormat.format(u.getFirstTimeStamp()));
            Log.d(TAG, "Pkg: " + u.getPackageName() + "\t" + "Last Time : "
                    + dateFormat.format(u.getLastTimeUsed()));
        }
    }

    public HashMap<ForegroundEvent,Integer> calcEvent(UsageEvents uEvents, long startPoint){

        UsageEvents.Event e = new UsageEvents.Event();

        HashMap<ForegroundEvent,Integer> map = new HashMap<>();

        boolean isNew = false;

        ForegroundEvent curE;

        String prevEvent = "packageName"; // to remove duplicated event

        while(uEvents.hasNextEvent()){
            uEvents.getNextEvent(e);

            if(!isNew && e.getTimeStamp() >= startPoint){
                isNew = true;
            }
            if(e != null){
                Log.d("TEST", "pkg : " + e.getPackageName() + " " + e.getEventType()+"time : " + e.getTimeStamp());
            }
            if(e != null && isNew){
                if(e.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND
                        && !prevEvent.equals(e.getPackageName())){
                    curE = new ForegroundEvent(e);
                    Integer count = map.get(curE);
                    if(count == null){
                        map.put(curE,1);
                    }else{
                        map.put(curE, count.intValue() + 1);
                    }
                    prevEvent = e.getPackageName();
                }
            }
        }
        return map;
    }

    public static long getCleanMillisecond(long millisecond){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millisecond);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        return cal.getTimeInMillis();
    }

    // @TODO
    // should update better algorithm
    // there is a problem with locale to compare with long
    public static boolean isSameDay(long date1, long date2) {

        // If they now are equal then it is the same day.
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.setTimeInMillis(date1);
        cal2.setTimeInMillis(date2);

        return cal1.DATE == cal2.DATE && cal1.MONTH == cal2.MONTH
                && cal1.YEAR == cal2.YEAR ? true : false;
    }

    private class ForegroundEvent{

        private String packageName;
        private long date;

        public ForegroundEvent(){}

        public ForegroundEvent(UsageEvents.Event e){
            this.packageName = e.getPackageName();
            this.date = e.getTimeStamp();
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
            result = 31 * result + (int) (getCleanMillisecond(date));
            return result;
        }

        @Override
        public boolean equals(Object obj) {

            if(obj instanceof ForegroundEvent){

                ForegroundEvent e = (ForegroundEvent)obj;

                if(e.getPackageName().equals(this.packageName) &&
                        isSameDay(e.getDate(), this.date)){
                    return true;
                }
                Log.d("WOW","here");
                return false;
            }
            return false;
        }

    }
}
