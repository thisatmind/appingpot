package com.thisatmind.appingpot.appingpot.rest.model;

/**
 * Created by patrick on 2016-10-02.
 */

public class AppInfo {
    String name;
    String model;

    public AppInfo(){}


    public AppInfo(String name, String model){
        this.name = name;
        this.model = model;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setModel(String model){
        this.model = model;
    }

    public String getModel(){
        return this.model;
    }
}
