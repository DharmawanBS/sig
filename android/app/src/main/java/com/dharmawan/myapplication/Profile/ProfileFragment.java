package com.dharmawan.myapplication.Profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.dharmawan.myapplication.Callback;
import com.dharmawan.myapplication.Constant;
import com.dharmawan.myapplication.EditText_Privilege;
import com.dharmawan.myapplication.ParseJson;
import com.dharmawan.myapplication.R;
import com.dharmawan.myapplication.Session;
import com.dharmawan.myapplication.TabActivity;
import com.dharmawan.myapplication.UserObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dharmawan on 12/3/17.
 */

public class ProfileFragment extends Fragment {

    ImageView photo;
    TextView status;
    EditText display_name;
    EditText username;
    EditText password;
    EditText re_password;
    Button edit;
    Button save;
    Button cancel;
    Button edit_status;
    TabActivity mainActivity;
    String st_name,st_username,st_status,st_photo,temp_status;
    boolean username_valid,password_valid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_,container,false);
        photo = rootView.findViewById(R.id.iv_profile_photo);
        status = rootView.findViewById(R.id.tv_profile_status);
        display_name = rootView.findViewById(R.id.et_profile_name);
        username = rootView.findViewById(R.id.et_profile_username);
        password = rootView.findViewById(R.id.et_profile_password);
        re_password = rootView.findViewById(R.id.et_profile_re_password);
        edit = rootView.findViewById(R.id.btn_edit);
        save = rootView.findViewById(R.id.btn_save_edit);
        cancel = rootView.findViewById(R.id.btn_cancel_edit);
        edit_status = rootView.findViewById(R.id.btn_edit_status);
        edit.setOnClickListener(op);
        save.setOnClickListener(op);
        cancel.setOnClickListener(op);
        edit_status.setOnClickListener(op);
        setHasOptionsMenu(true);
        canEdit(false);
        getData();
        mainActivity = (TabActivity)getActivity();
        username_valid = false;
        password_valid = false;
        return rootView;
    }

    private void canEdit(boolean action) {
        username.setEnabled(action);
        password.setEnabled(action);
        display_name.setEnabled(action);
        re_password.setEnabled(action);

        if (action) {
            (new EditText_Privilege(display_name, "Tidak boleh kosong")).setPrivilege();
            (new EditText_Privilege(username, "Tidak boleh kosong")).setPrivilege();
            (new EditText_Privilege(password, "Tidak boleh kosong")).setPrivilege();
            (new EditText_Privilege(re_password, "Tidak boleh kosong")).setPrivilege();
        }
    }

    View.OnClickListener op = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.btn_edit:
                    edit.setVisibility(View.GONE);
                    edit_status.setVisibility(View.GONE);
                    save.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.VISIBLE);
                    canEdit(true);
                    break;
                case R.id.btn_save_edit:
                    edit.setVisibility(View.VISIBLE);
                    edit_status.setVisibility(View.VISIBLE);
                    save.setVisibility(View.GONE);
                    cancel.setVisibility(View.GONE);
                    canEdit(false);
                    edit_profil();
                    break;
                case R.id.btn_edit_status:
                    edit_status();
                    break;
                case R.id.btn_cancel_edit:
                    edit.setVisibility(View.VISIBLE);
                    edit_status.setVisibility(View.VISIBLE);
                    save.setVisibility(View.GONE);
                    cancel.setVisibility(View.GONE);
                    canEdit(false);
                    setView();
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void getData(){
        final Session session = new Session(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL+"/User/profil", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("profile",response);
                fetchData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
        if(msg.equals("OK")){
            final UserObject Data = parseJson.ProfileParse();
            st_name = Data.getName();
            st_username = Data.getUsername();
            st_status = Data.getLast_status();
            if (!Data.getPhoto().equals("null")) st_photo = Data.getPhoto();
            else st_photo = "";
            setView();
            Session session = new Session(getContext());
            session.editPreferences(session.getPreferences("token"),session.getPreferences("username"),session.getPreferences("password"),Data.getId());
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

    private void setView() {
        display_name.setText(st_name);
        username.setText(st_username);
        status.setText(st_status);
        if (!st_photo.equals("")) {
            Glide.with(getContext()).load(Constant.URL + "/photo/" + st_photo).into(photo);
        }
        else {
            photo.setImageResource(R.drawable.icon);
        }
    }

    private void edit_status() {

    }

    private void updateStatus(){
        final Session session = new Session(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL+"/User/edit_status", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("edit_status",response);
                fetchData2(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
                param.put("status",temp_status);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void fetchData2(String response){
        ParseJson parseJson = new ParseJson(response);
        String msg = parseJson.parseLogin() !=null ? parseJson.parseLogin() : "";
        if(msg.equals("OK")){
            st_status = temp_status;
            setView();
            Session session = new Session(getContext());
            session.editPreferences(session.getPreferences("token"),session.getPreferences("username"),session.getPreferences("password"),session.getPreferences("id"));
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

    private void check_username() {
        final Session session = new Session(getContext());
        session.check_username(username.getText().toString(),new Callback() {
            @Override
            public void onSuccessResponse(String result) {
                ParseJson pj = new ParseJson(result);
                String msg = pj.parseLogin() != null ? pj.parseLogin() : "";
                if(!msg.equals("OK")){
                    username.setError("Ganti username");
                    username_valid = false;
                }
                else  {
                    ParseJson parseJson = new ParseJson(result);
                    String msg1 = parseJson.parseLogin() !=null ? parseJson.parseLogin() : "";
                    if(msg1.equals("OK")){
                        username_valid = true;
                    }
                    else{
                        username.setError("Ganti username");
                        username_valid = false;
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Terjadi kesalahan, coba cek koneksi anda",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void edit_profil() {
        if (!username_valid) {
            check_username();
            username.setError("Ganti username");
        }

        password_valid = password.getText().toString().equals(re_password.getText().toString());
        if (!password_valid) {
            password.setError("Password harus sama");
            re_password.setError("Password harus sama");
        }

        if (username_valid && password_valid) {
            updateProfil();
        }
    }

    private void updateProfil(){
        final Session session = new Session(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL+"/User/edit_profil", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("edit_profil",response);
                fetchData2(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
                param.put("username",username.getText().toString());
                param.put("password",password.getText().toString());
                param.put("name",display_name.getText().toString());
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void fetchData3(String response){
        ParseJson parseJson = new ParseJson(response);
        String msg = parseJson.parseLogin() !=null ? parseJson.parseLogin() : "";
        if(msg.equals("OK")){
            setView();
            Session session = new Session(getContext());
            session.editPreferences(session.getPreferences("token"),session.getPreferences("username"),session.getPreferences("password"),session.getPreferences("id"));
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
