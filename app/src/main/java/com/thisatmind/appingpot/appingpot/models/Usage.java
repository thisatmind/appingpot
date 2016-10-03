package com.thisatmind.appingpot.appingpot.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Patrick on 2016-09-17.
 */
public class Usage extends RealmObject{

    @PrimaryKey
    @Required
    private String key;
    private String packageName;
    private long date;

    private long usage;

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

    public long getUsage() {
        return usage;
    }

    public void setUsage(long usage) {
        this.usage = usage;
    }

    public String getKey() { return key; }

    public void setKey(String key) { this.key = key; }

    public String genKey(String packageName, long date){
        return packageName + date;
    }

}
