package com.dharmawan.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class RegisterActivity extends AppCompatActivity {

    EditText reg_name;
    EditText username;
    EditText password;
    EditText re_password;
    ImageButton refresh;
    Button register;
    Button cancel;
    boolean username_valid;
    boolean password_valid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
        setAction();
    }

    private void init() {
        reg_name = findViewById(R.id.et_reg_name);
        username = findViewById(R.id.et_reg_username);
        password = findViewById(R.id.et_reg_password);
        re_password = findViewById(R.id.et_reg_password2);
        refresh = findViewById(R.id.ib_refresh);
        register = findViewById(R.id.btn_sigup);
        cancel = findViewById(R.id.btn_cancel);
        username_valid = false;
        password_valid = false;
    }

    private void setAction() {
        (new EditText_Privilege(reg_name,"Tidak boleh kosong")).setPrivilege();
        (new EditText_Privilege(username,"Tidak boleh kosong")).setPrivilege();
        (new EditText_Privilege(password,"Tidak boleh kosong")).setPrivilege();
        (new EditText_Privilege(re_password,"Tidak boleh kosong")).setPrivilege();
        refresh.setOnClickListener(op);
        register.setOnClickListener(op);
        cancel.setOnClickListener(op);
    }

    View.OnClickListener op = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.ib_refresh:
                    check_username();
                    break;
                case R.id.btn_cancel:
                    finish();
                    break;
                case R.id.btn_sigup:
                    if (!username_valid) {
                        check_username();
                        username.setError("Ganti username");
                    }
                    else {
                        username.setError("OK");
                    }

                    password_valid = password.getText().toString().equals(re_password.getText().toString());
                    if (!password_valid) {
                        password.setError("Password harus sama");
                        re_password.setError("Password harus sama");
                    }

                    if (username_valid && password_valid) {
                        sign_up();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void check_username() {
        final Session session = new Session(getBaseContext());
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
                    fetchData(result);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(),"Terjadi kesalahan, coba cek koneksi anda",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sign_up() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL+"/User/register", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("register",response);
                fetchData2(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("username",username.getText().toString());
                param.put("password",password.getText().toString());
                param.put("name",reg_name.getText().toString());
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
        requestQueue.add(stringRequest);
    }

    private void fetchData(String response){
        ParseJson parseJson = new ParseJson(response);
        String msg = parseJson.parseLogin() !=null ? parseJson.parseLogin() : "";
        if(msg.equals("OK")){
            username_valid = true;
        }
        else{
            username_valid = false;
            username.setError("Ganti username");
        }
    }

    private void fetchData2(String response){
        ParseJson parseJson = new ParseJson(response);
        String msg = parseJson.parseLogin() !=null ? parseJson.parseLogin() : "";
        if(msg.equals("OK")){
            Toast.makeText(RegisterActivity.this, "Sign up sukses", Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            username.setError("Ganti username");
        }
    }
}
