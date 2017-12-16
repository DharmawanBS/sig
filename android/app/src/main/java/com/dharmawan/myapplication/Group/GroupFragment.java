package com.dharmawan.myapplication.Group;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dharmawan.myapplication.Callback;
import com.dharmawan.myapplication.Constant;
import com.dharmawan.myapplication.ParseJson;
import com.dharmawan.myapplication.R;
import com.dharmawan.myapplication.Session;
import com.dharmawan.myapplication.TabActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dharmawan on 12/3/17.
 */

public class GroupFragment extends Fragment{

    FloatingActionButton fab;
    RecyclerView rv;
    SwipeRefreshLayout refresher;
    TabActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_,container,false);
        rv = rootView.findViewById(R.id.rv_group);
        refresher = rootView.findViewById(R.id.group_refresh);
        setHasOptionsMenu(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        getData();
        mainActivity = (TabActivity)getActivity();
        refresher.setOnRefreshListener(refresh);
        refresher.post(new Runnable() {
            @Override
            public void run() {
                refresher.setRefreshing(true);
                getData();
            }
        });
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    SwipeRefreshLayout.OnRefreshListener refresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            getData();
        }
    };

    private void getData(){
        final Session session = new Session(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL+"/Group/get_group", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("group",response);
                refresher.setRefreshing(false);
                fetchData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                refresher.setRefreshing(false);
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("token",session.getPreferences("token"));
                return header;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void fetchData(String response){
        ParseJson parseJson = new ParseJson(response);
        String msg = parseJson.parseLogin() !=null ? parseJson.parseLogin() : "";
        if(msg.equals("OK") || msg.equals("EMPTY")){
            final List<GroupObject> listData = parseJson.listGroupParse();
            GroupAdapter groupAdapter = new GroupAdapter(getContext(),listData);
            rv.setAdapter(groupAdapter);
            Session session = new Session(getContext());
        }
        else if(msg.equals("UNAUTHORIZED")){
            final Session session = new Session(getContext());
            session.loginProcess(session.getPreferences("username"), session.getPreferences("password"), new Callback() {
                @Override
                public void onSuccessResponse(String result) {
                    ParseJson pj = new ParseJson(result);
                    String token = pj.parseToken();
                    session.editPreferences(token,session.getPreferences("username"),session.getPreferences("password"),"");
                    getData();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(),"Terjadi kesalahan, coba cek koneksi anda",Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(getContext(),"Terjadi kesalahan, coba cek koneksi anda",Toast.LENGTH_SHORT).show();
        }
    }
}
