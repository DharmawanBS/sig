package com.dharmawan.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dharmawan on 12/3/17.
 */

public class Session {
    private Context context;
    private SharedPreferences sharedPreferences;

    public Session(Context context) {
        if (context!=null){
            this.context = context;
            sharedPreferences = this.context.getSharedPreferences("myToken",Context.MODE_PRIVATE);
        }
        else{
            logout();
        }
    }

    public boolean isAvailable(){
        boolean returnVal;
        String value = sharedPreferences.getString("token","empty");
        returnVal = !value.equals("empty");
        return returnVal;
    }

    public void editPreferences(String token,String username,String password,String id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token",token);
        editor.putString("username",username);
        editor.putString("password",password);
        editor.putString("id",id);
        editor.apply();
    }


    public void removePreferences(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public String getPreferences(String param){
        String returnVal = null;
        switch (param){
            case "token" : returnVal = sharedPreferences.getString("token",null);break;
            case "username" : returnVal = sharedPreferences.getString("username",null);break;
            case "password" : returnVal = sharedPreferences.getString("password",null);break;
            case "id" : returnVal = sharedPreferences.getString("id",null);break;
            default: break;
        }
        return returnVal;
    }

    public void loginProcess(final String username, final String password,final Callback callback){
        StringRequest newReq = new StringRequest(Request.Method.POST, Constant.URL+"/User/login", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccessResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onErrorResponse(error);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("username",username);
                param.put("password",password);
                return param;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(newReq);
    }

    public void check_username(final String username,final Callback callback) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL+"/User/check_username", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("check_username",response);
                callback.onSuccessResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onErrorResponse(error);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("username",username);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void logout(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL+"User/logout", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ParseJson pj = new ParseJson(response);
                removePreferences();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(context != null){
                    Toast.makeText(context,error.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("token",getPreferences("token"));
                return header;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
