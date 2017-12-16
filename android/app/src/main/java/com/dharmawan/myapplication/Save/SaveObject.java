package com.dharmawan.myapplication.Save;

import com.dharmawan.myapplication.TrackUser;

import java.util.List;

/**
 * Created by dharmawan on 12/8/17.
 */

public class SaveObject {
    String title;
    String datetime;
    String id;
    List<TrackUser> user;

    public SaveObject(String title, String datetime, String id, List<TrackUser> track) {
        this.title = title;
        this.datetime = datetime;
        this.id = id;
        this.user = track;
    }

    public String getTitle() {
        return title;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getId() {
        return id;
    }

    public List<TrackUser> getUser() {
        return user;
    }
}
