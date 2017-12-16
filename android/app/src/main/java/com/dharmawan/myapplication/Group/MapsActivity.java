package com.dharmawan.myapplication.Group;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener,OnMapReadyCallback {

    private GoogleMap mMap;
    Button start;
    Button stop;
    Button be_leader;
    Button view;

    LocationManager mylocationManager;
    LocationListener mylocationListener;

    Session session;

    String id;
    boolean leader;
    boolean update;
    Integer time,distance,zoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString("id");
            leader = bundle.getBoolean("leader");
        }
        else {
            finish();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.f_map);
        mapFragment.getMapAsync(this);

        start = findViewById(R.id.btn_start);
        stop = findViewById(R.id.btn_stop);
        be_leader = findViewById(R.id.btn_be_leader);
        view = findViewById(R.id.btn_view);

        start.setOnClickListener(op);
        stop.setOnClickListener(op);
        be_leader.setOnClickListener(op);
        view.setOnClickListener(op);
        if (leader) be_leader.setVisibility(View.GONE);

        mylocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mylocationListener = new loc_listener();

        session = new Session(getBaseContext());

        update = false;
        time = 0;
        distance = 0;
        zoom = 15;
    }

    View.OnClickListener op = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.btn_start:
                    stop.setVisibility(View.VISIBLE);
                    start.setVisibility(View.GONE);
                    GPS_active(true);
                    update = true;
                    mMap.clear();
                    break;
                case R.id.btn_stop:
                    start.setVisibility(View.VISIBLE);
                    stop.setVisibility(View.GONE);
                    GPS_active(false);
                    update = false;
                    break;
                case R.id.btn_be_leader:
                    become_leader();
                    break;
                case R.id.btn_view:
                    mMap.clear();
                    new_location();
                    break;
                default:
                    break;
            }
        }
    };

    private void become_leader() {
        final Session session = new Session(getBaseContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL+"/Group/be_leader", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("be_leader",response);
                ParseJson pj = new ParseJson(response);
                String msg = pj.parseLogin() != null ? pj.parseLogin() : "";
                if(!msg.equals("OK")){
                    Toast.makeText(getBaseContext(),"Terjadi kesalahan, coba cek koneksi anda",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getBaseContext(),"You are leader now",Toast.LENGTH_SHORT).show();
                    be_leader.setClickable(false);
                }
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
                param.put("group",id);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
        requestQueue.add(stringRequest);
    }

    private void GPS_active(boolean action) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (action) {
            boolean gps_enabled = false;
            boolean network_enabled = false;
            gps_enabled = mylocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = mylocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (gps_enabled) {
                mylocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, distance, mylocationListener);
                Toast.makeText(getBaseContext(),"GPS Aktif Time: "+time.toString()+" Range: "+distance.toString(),Toast.LENGTH_LONG).show();
            }
            else if (network_enabled) {
                mylocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, time, distance, mylocationListener);
                Toast.makeText(getBaseContext(),"Menggunakan Network Provider Aktif Time: "+time.toString()+" Range: "+distance.toString(),Toast.LENGTH_LONG).show();
            }
        }
        else {
            mylocationManager.removeUpdates(mylocationListener);
            Toast.makeText(getBaseContext(),"GPS NonAktif",Toast.LENGTH_LONG).show();
        }
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
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    private class loc_listener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            Toast.makeText(getBaseContext(), "Updated", Toast.LENGTH_SHORT).show();

            mMap.clear();

            if (update) {
                update_location(lat, lng);
                new_location();
            }
            else {
                LatLng cur_pos = new LatLng(lat,lng);
                //mMap.addMarker(new MarkerOptions().position(cur_pos));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cur_pos, zoom));
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            
        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.sattelite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.hibryd:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.none:
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void update_location(final double lat, final double lng) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL+"/Share_location/update_loc", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("update_loc",response);
                ParseJson pj = new ParseJson(response);
                String msg = pj.parseLogin() != null ? pj.parseLogin() : "";
                if(!msg.equals("OK")){
                    Toast.makeText(getBaseContext(),"Terjadi kesalahan, coba cek koneksi anda",Toast.LENGTH_SHORT).show();
                }
                else  {
                    Toast.makeText(getBaseContext(), "Push Updated", Toast.LENGTH_SHORT).show();
                }
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
                param.put("group",id);
                param.put("user",session.getPreferences("id"));
                param.put("latitude",String.valueOf(lat));
                param.put("longitude",String.valueOf(lng));
                Log.d("send_update",id + " " + session.getPreferences("id") + " " + String.valueOf(lat) + " " + String.valueOf(lng) + " " + session.getPreferences("token"));
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
        requestQueue.add(stringRequest);
    }

    private void new_location() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL+"/Share_location/get_loc", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("get_loc",response);
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
                param.put("group",id);
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
            final List<TrackUser> listData = parseJson.listGroupTrackParse();
            update_map(listData);
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
                String is_leader = listData.get(i).getIs_leader();
                String id = listData.get(i).getUser_id();
                for (int j = 0; j < tr.size(); j++) {
                    double lat = Double.parseDouble(tr.get(j).getLatitude());
                    double lng = Double.parseDouble(tr.get(j).getLongitude());
                    LatLng position = new LatLng(lat, lng);
                    if (j == 0) akhir = position;
                    points.add(position);
                }
                int warna = Color.BLACK;
                if (id.equals(session.getPreferences("id"))) {
                    warna = Color.BLUE;
                    current = akhir;
                }
                else if (is_leader.equals("true")) {
                    warna = Color.GREEN;
                }
                lineOptions.addAll(points).width(10).color(warna);

                // Drawing polyline in the Google Map for the i-th route
                if (akhir != null && !id.equals(session.getPreferences("id"))) {
                    mMap.addMarker(new MarkerOptions().position(akhir).title(listData.get(i).getUser_display_name()));
                }
                else if (akhir != null && id.equals(session.getPreferences("id"))) {
                    mMap.addMarker(new MarkerOptions().position(akhir).title(listData.get(i).getUser_display_name()).icon(BitmapDescriptorFactory.fromResource(R.drawable.rsz_icon)));
                }
                mMap.addPolyline(lineOptions);
            }
            if (current != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, zoom));
            }
        }
    }
}