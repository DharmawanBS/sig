package com.dharmawan.myapplication;

import java.util.List;

/**
 * Created by dharmawan on 12/4/17.
 */

public class TrackUser {
    String user_id;
    String user_display_name;
    String user_photo;
    String is_leader;
    List<TrackObject> track;

    public TrackUser(String user_id, String user_display_name, String user_photo, String is_leader, List<TrackObject> track) {
        this.user_id = user_id;
        this.user_display_name = user_display_name;
        this.user_photo = user_photo;
        this.is_leader = is_leader;
        this.track = track;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_display_name() {
        return user_display_name;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public String getIs_leader() {
        return is_leader;
    }

    public List<TrackObject> getTrack() {
        return track;
    }
}