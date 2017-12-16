package com.dharmawan.myapplication;

import com.dharmawan.myapplication.Group.GroupObject;
import com.dharmawan.myapplication.Save.SaveObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dharmawan on 12/3/17.
 */

public class ParseJson {
    private String json;

    public ParseJson(String json) {
        this.json = json;
    }

    public String parseLogin(){
        JSONObject jObject;
        String parseVal = null;
        try {
            jObject = new JSONObject(json);
            parseVal = jObject.getString("msg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseVal;
    }
    public String parseToken(){
        JSONObject jsonObject;
        String parseVal = null;
        try {
            jsonObject = new JSONObject(json);
            parseVal = jsonObject.getString("DATA");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseVal;
    }

    public List<UserObject> listFriendParse(){
        JSONObject jsonObject;
        List<UserObject> parseval = new ArrayList<>();
        try {
            jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("DATA");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jRow = jsonArray.getJSONObject(i);
                UserObject friend = new UserObject(
                        jRow.getString("user_id"),
                        null,
                        jRow.getString("user_reg_datetime"),
                        jRow.getString("user_last_status"),
                        jRow.getString("user_display_name"),
                        jRow.getString("user_photo"),
                        jRow.getString("datetime"));
                parseval.add(friend);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseval;
    }

    public UserObject ProfileParse() {
        JSONObject jsonObject;
        UserObject parseval = null;
        try {
            jsonObject = new JSONObject(json);
            JSONObject jRow = jsonObject.getJSONObject("DATA");
            parseval = new UserObject(
                    jRow.getString("user_id"),
                    jRow.getString("user_name"),
                    jRow.getString("user_reg_datetime"),
                    jRow.getString("user_last_status"),
                    jRow.getString("user_display_name"),
                    jRow.getString("user_photo"),
                    null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseval;
    }

    public List<GroupObject> listGroupParse(){
        JSONObject jsonObject;
        List<GroupObject> parseval = new ArrayList<>();
        try {
            jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("DATA");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jRow = jsonArray.getJSONObject(i);

                ArrayList<UserObject> leader = new ArrayList<>();
                try {
                    JSONArray jsonArray_leader = jRow.getJSONArray("leader");
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jRow_leader = jsonArray_leader.getJSONObject(j);
                        UserObject temp_leader = new UserObject(
                                jRow_leader.getString("user_id"),
                                null,
                                jRow_leader.getString("user_reg_datetime"),
                                jRow_leader.getString("user_last_status"),
                                jRow_leader.getString("user_display_name"),
                                jRow_leader.getString("user_photo"),
                                null);
                        leader.add(temp_leader);
                    }
                }
                catch (JSONException ignored) {
                    ignored.printStackTrace();
                }

                ArrayList<UserObject> member = new ArrayList<>();
                try {
                    JSONArray jsonArray_member = jRow.getJSONArray("member");
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jRow_member = jsonArray_member.getJSONObject(j);
                        UserObject temp_member = new UserObject(
                                jRow_member.getString("user_id"),
                                null,
                                jRow_member.getString("user_reg_datetime"),
                                jRow_member.getString("user_last_status"),
                                jRow_member.getString("user_display_name"),
                                jRow_member.getString("user_photo"),
                                null);
                        leader.add(temp_member);
                    }
                }
                catch (JSONException ignored) {
                    ignored.printStackTrace();
                }

                GroupObject group = new GroupObject(
                        jRow.getString("group_id"),
                        jRow.getString("group_name"),
                        leader.size(),
                        member.size(),
                        leader,
                        member);
                parseval.add(group);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseval;
    }

    public List<TrackUser> listGroupTrackParse(){
        JSONObject jsonObject;
        List<TrackUser> parseval = new ArrayList<>();
        try {
            jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("DATA");

            String temp_id = "";
            String temp_name = "";
            String temp_photo = "";
            String temp_is_leader = "";
            List<TrackObject> obj = new ArrayList<>();
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jRow = jsonArray.getJSONObject(i);

                if (temp_id.equals("")) {
                    temp_id = jRow.getString("user_id");
                    temp_name = jRow.getString("user_display_name");
                    temp_photo = jRow.getString("user_photo");
                    temp_is_leader = jRow.getString("is_leader");

                    TrackObject ob = new TrackObject(
                            jRow.getString("loc_datetime"),
                            jRow.getString("loc_latitude"),
                            jRow.getString("loc_longitude"));
                    obj.add(ob);
                }
                else if (temp_id.equals(jRow.getString("user_id"))) {
                    TrackObject ob = new TrackObject(
                            jRow.getString("loc_datetime"),
                            jRow.getString("loc_latitude"),
                            jRow.getString("loc_longitude"));
                    obj.add(ob);
                }
                else {
                    TrackUser user = new TrackUser(
                            temp_id,
                            temp_name,
                            temp_photo,
                            temp_is_leader,
                            obj);
                    parseval.add(user);

                    temp_id = jRow.getString("user_id");
                    temp_name = jRow.getString("user_display_name");
                    temp_photo = jRow.getString("user_photo");
                    temp_is_leader = jRow.getString("is_leader");
                    obj = new ArrayList<>();
                }
            }
            TrackUser user = new TrackUser(
                    temp_id,
                    temp_name,
                    temp_photo,
                    temp_is_leader,
                    obj);
            parseval.add(user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseval;
    }

    public List<SaveObject> listSaveParse(){
        JSONObject jsonObject;
        List<SaveObject> parseval = new ArrayList<>();
        try {
            jsonObject = new JSONObject(json);
            JSONArray jsonArraynya = jsonObject.getJSONArray("DATA");
            for(int i=0;i<jsonArraynya.length();i++){
                JSONObject jRownya = jsonArraynya.getJSONObject(i);

                List<TrackUser> usr = new ArrayList<>();
                try{
                    JSONArray jsonArray = jRownya.getJSONArray("location");
                    String temp_id = "";
                    String temp_name = "";
                    String temp_photo = "";
                    List<TrackObject> obj = new ArrayList<>();
                    for(int j=0;j<jsonArray.length();j++){
                        JSONObject jRow = jsonArray.getJSONObject(j);

                        if (temp_id.equals("")) {
                            temp_id = jRow.getString("user_id");
                            temp_name = jRow.getString("user_display_name");
                            temp_photo = jRow.getString("user_photo");

                            TrackObject ob = new TrackObject(
                                    jRow.getString("loc_datetime"),
                                    jRow.getString("loc_latitude"),
                                    jRow.getString("loc_longitude"));
                            obj.add(ob);
                        }
                        else if (temp_id.equals(jRow.getString("user_id"))) {
                            TrackObject ob = new TrackObject(
                                    jRow.getString("loc_datetime"),
                                    jRow.getString("loc_latitude"),
                                    jRow.getString("loc_longitude"));
                            obj.add(ob);
                        }
                        else {
                            TrackUser user = new TrackUser(
                                    temp_id,
                                    temp_name,
                                    temp_photo,
                                    null,
                                    obj);
                            usr.add(user);

                            temp_id = jRow.getString("user_id");
                            temp_name = jRow.getString("user_display_name");
                            temp_photo = jRow.getString("user_photo");
                            obj = new ArrayList<>();
                        }
                    }
                    TrackUser user = new TrackUser(
                            temp_id,
                            temp_name,
                            temp_photo,
                            null,
                            obj);
                    usr.add(user);
                }
                catch (JSONException ignored) {
                    ignored.printStackTrace();
                }

                SaveObject save = new SaveObject(
                        jRownya.getString("travel_id"),
                        jRownya.getString("travel_title"),
                        jRownya.getString("travel_datetime"),
                        usr);
                parseval.add(save);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseval;
    }
}