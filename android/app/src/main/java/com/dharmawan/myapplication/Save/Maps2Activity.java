package com.dharmawan.myapplication.Save;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.dharmawan.myapplication.TrackObject;
import com.dharmawan.myapplication.TrackUser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Maps2Activity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Session session;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString("id");
        }
        else {
            finish();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.clear();
        new_location();
    }

    private void new_location() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL+"/Share_location/get_loc2", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("get_loc2",response);
                fetchData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(),"Terjadi kesalahan, coba cek koneksi anda",Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("token",session.getPreferences("token"));
                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("travel",id);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
        requestQueue.add(stringRequest);
    }

    private void fetchData(String response) {
        ParseJson parseJson = new ParseJson(response);
        String msg = parseJson.parseLogin() !=null ? parseJson.parseLogin() : "";
        if(msg.equals("OK")){
            Toast.makeText(getBaseContext(), "Updated", Toast.LENGTH_SHORT).show();
            final List<SaveObject> listData = parseJson.listSaveParse();
            if (listData.size() > 0) update_map(listData.get(0).getUser());
            else {
                mMap.clear();
                Toast.makeText(getBaseContext(), "Tidak ada data", Toast.LENGTH_SHORT).show();
            }
        }
        else if(msg.equals("EMPTY")){
            mMap.clear();
            Toast.makeText(getBaseContext(), "Tidak ada data", Toast.LENGTH_SHORT).show();
        }
        else if(msg.equals("UNAUTHORIZED")){
            session.loginProcess(session.getPreferences("username"), session.getPreferences("password"), new Callback() {
                @Override
                public void onSuccessResponse(String result) {
                    ParseJson pj = new ParseJson(result);
                    String token = pj.parseToken();
                    session.editPreferences(token,session.getPreferences("username"),session.getPreferences("password"),session.getPreferences("id"));
                    new_location();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getBaseContext(),"Terjadi kesalahan, coba cek koneksi anda",Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(getBaseContext(),"Terjadi kesalahan, coba cek koneksi anda",Toast.LENGTH_SHORT).show();
        }
    }

    private void update_map(List<TrackUser> listData) {
        Log.d("total",String.valueOf(listData.size()));
        mMap.clear();
        LatLng current = null;
        if (listData.size() > 0) {
            for(int i=0;i<listData.size();i++){
                PolylineOptions lineOptions = new PolylineOptions();
                LatLng akhir = null;
                ArrayList<LatLng> points = new ArrayList<>();

                List<TrackObject> tr = listData.get(i).getTrack();
                String id = listData.get(i).getUser_id();
                for (int j = 0; j < tr.size(); j++) {
                    double lat = Double.parseDouble(tr.get(j).getLatitude());
                    double lng = Double.parseDouble(tr.get(j).getLongitude());
                    LatLng position = new LatLng(lat, lng);
                    if (j == tr.size()-1) akhir = position;
                    points.add(position);
                }
                int warna = Color.BLACK;
                if (id.equals(session.getPreferences("id"))) {
                    warna = Color.BLUE;
                    current = akhir;
                }
                lineOptions.addAll(points).width(10).color(warna);

                // Drawing polyline in the Google Map for the i-th route
                if (akhir != null) {
                    mMap.addMarker(new MarkerOptions().position(akhir));
                }
                mMap.addPolyline(lineOptions);
            }
            if (current != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15));
            }
        }
    }
}
