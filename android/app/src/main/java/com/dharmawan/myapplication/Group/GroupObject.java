package com.dharmawan.myapplication.Group;

import com.dharmawan.myapplication.UserObject;

import java.util.ArrayList;

/**
 * Created by dharmawan on 12/3/17.
 */

public class GroupObject {
    String id;
    String name;
    Integer leader_count;
    Integer member_count;
    ArrayList<UserObject> leader;
    ArrayList<UserObject> member;

    public GroupObject(String id, String name, Integer leader_count, Integer member_count, ArrayList<UserObject> leader, ArrayList<UserObject> member) {
        this.id = id;
        this.name = name;
        this.leader_count = leader_count;
        this.member_count = member_count;
        this.leader = leader;
        this.member = member;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getLeader_count() {
        return leader_count;
    }

    public Integer getMember_count() {
        return member_count;
    }

    public ArrayList<UserObject> getLeader() {
        return leader;
    }

    public ArrayList<UserObject> getMember() {
        return member;
    }
}
