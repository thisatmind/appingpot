package com.thisatmind.appingpot.appingpot.pojo;

/**
 * Created by Patrick on 2016-09-17.
 */
public class AppCount {
    private final String packageName;
    private final int count;

    public AppCount(String packageName, int count) {
        this.packageName = packageName;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public String getPackageName() {

        return packageName;
    }
}
