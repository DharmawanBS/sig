package com.dharmawan.myapplication;

/**
 * Created by dharmawan on 12/3/17.
 */

public class UserObject {
    String id;
    String username;
    String reg_datetime;
    String last_status;
    String name;
    String photo;
    String datetime;

    public UserObject(String id, String username, String reg_datetime, String last_status, String name, String photo, String datetime) {
        this.id = id;
        this.username = username;
        this.reg_datetime = reg_datetime;
        this.last_status = !last_status.equals("null") ? last_status : "";
        this.name = name;
        this.photo = photo;
        this.datetime = datetime;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getReg_datetime() {
        return reg_datetime;
    }

    public String getLast_status() {
        return last_status;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public String getDatetime() {
        return datetime;
    }
}
