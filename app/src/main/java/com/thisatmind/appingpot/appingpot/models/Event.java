package com.thisatmind.appingpot.appingpot.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Patrick on 2016-09-17.
 */
public class Event extends RealmObject{

    @PrimaryKey
    @Required
    private String pacakageName;
    private Date date;

    private int count;

    public String getPacakageName() {
        return pacakageName;
    }

    public void setPacakageName(String pacakageName) {
        this.pacakageName = pacakageName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
