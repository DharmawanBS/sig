package com.dharmawan.myapplication;

/**
 * Created by dharmawan on 12/4/17.
 */

public class TrackObject {
    String datetime;
    String latitude;
    String longitude;

    public TrackObject(String datetime, String latitude, String longitude) {
        this.datetime = datetime;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
