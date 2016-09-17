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
import java.util.List;
import java.util.Locale;

/**
 * Created by Patrick on 2016-09-17.
 */
public class Tracker {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss", Locale.KOREA);

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

    public static void getUsageEvents(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, -1);
        long startTime = calendar.getTimeInMillis();

        Log.d(TAG, "Range start : " + dateFormat.format(startTime));
        Log.d(TAG, "Range end : " + dateFormat.format(endTime));

        UsageEvents uEvents = usm.queryEvents(startTime, endTime);
        UsageEvents.Event e = new UsageEvents.Event();

        while(uEvents.hasNextEvent()){
            uEvents.getNextEvent(e);

            if(e != null){
                Log.d(TAG, e.getEventType() + "/ Event : " + e.getPackageName() + " / " + dateFormat.format(e.getTimeStamp()));
            }
        }

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



}
