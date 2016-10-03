package com.thisatmind.appingpot.appingpot.rest.model;

/**
 * Created by patrick on 2016-10-02.
 */

public class Event {


    private String key;

    private String packageName;

    private long date;

    private int count;

    public Event(){}

    public Event(String key, String packageName, long date, int count){
        this.key = key;
        this.packageName = packageName;
        this.date = date;
        this.count = count;
    }

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getKey() { return key; }

    public void setKey(String key) { this.key = key; }

    public String genKey(String packageName, long date){
        return packageName + date;
    }
}
