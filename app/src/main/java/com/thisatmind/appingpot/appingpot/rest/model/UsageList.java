package com.thisatmind.appingpot.appingpot.rest.model;

import com.thisatmind.appingpot.appingpot.models.Usage;

import java.util.List;

/**
 * Created by patrick on 2016-09-25.
 */
public class UsageList {

    String userName;
    List<Usage> list;

    public UsageList(){}
    public UsageList(String userName, List<Usage> list) {
        this.userName = userName;
        this.list = list;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Usage> getList() {
        return list;
    }

    public void setList(List<Usage> list) {
        this.list = list;
    }
}
