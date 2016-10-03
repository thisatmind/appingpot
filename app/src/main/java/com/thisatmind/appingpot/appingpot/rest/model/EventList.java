package com.thisatmind.appingpot.appingpot.rest.model;

import java.util.List;

/**
 * Created by patrick on 2016-10-02.
 */

public class EventList {
    String userName;
    List<Event> list;

    public EventList(){}

    public EventList(String userName, List<Event> list){
        this.userName = userName;
        this.list = list;
    }

    public String getUsername(){
        return this.userName;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public List<Event> getEventList(){
        return this.list;
    }

    public void setEventList(List<Event> list){
        this.list = list;
    }
}
