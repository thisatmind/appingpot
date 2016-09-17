package com.thisatmind.appingpot.appingpot.models;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by Patrick on 2016-09-17.
 */
public class Tracker extends RealmObject{

    @Required
    private long startPoint;

    public Tracker(long startPoint){
        this.startPoint = startPoint;
    }

    public long getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(long startPoint) {
        this.startPoint = startPoint;
    }
}
