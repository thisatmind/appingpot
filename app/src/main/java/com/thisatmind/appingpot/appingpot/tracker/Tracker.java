package com.thisatmind.appingpot.appingpot.tracker;

import android.app.Activity;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.List;

/**
 * Created by Patrick on 2016-09-17.
 */
public class Tracker {

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

    public void getStats(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
    }
}
