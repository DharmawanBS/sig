package com.dharmawan.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

public class MainActivity extends AppCompatActivity {
    Button login;
    EditText username;
    EditText password;
    TextView sign_up;
    ImageButton view;
    ImageButton unview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        setAction();
    }

    private void init() {
        login = findViewById(R.id.btn_login);
        username = findViewById(R.id.et_username);
        password = findViewById(R.id.et_password);
        sign_up = findViewById(R.id.tv_sign_up);
        view = findViewById(R.id.ib_view);
        unview = findViewById(R.id.ib_unview);
    }

    View.OnClickListener op = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.tv_sign_up:
                    openIntent(RegisterActivity.class);
                    break;
                case R.id.ib_view:
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    view.setVisibility(View.GONE);
                    unview.setVisibility(View.VISIBLE);
                    break;
                case R.id.ib_unview:
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    unview.setVisibility(View.GONE);
                    view.setVisibility(View.VISIBLE);
                    break;
                case R.id.btn_login:
                    Toast.makeText(getBaseContext(),"Logging In . . .",Toast.LENGTH_SHORT).show();

                    if (username.getText().toString().trim().length()==0 || password.getText().toString().length()==0) {
                        Toast.makeText(getBaseContext(),"Username atau password kurang",Toast.LENGTH_LONG).show();
                    }
                    else {
                        final Session session = new Session(getBaseContext());
                        session.loginProcess(username.getText().toString(), password.getText().toString(), new Callback() {
                            @Override
                            public void onSuccessResponse(String result) {
                                ParseJson pj = new ParseJson(result);
                                String msg = pj.parseLogin() != null ? pj.parseLogin() : "";
                                if(!msg.equals("OK")){
                                    Toast.makeText(getBaseContext(),"username atau password salah",Toast.LENGTH_SHORT).show();
                                }
                                else  {
                                    String token = pj.parseToken();
                                    openIntent(TabActivity.class);
                                    session.removePreferences();
                                    Log.d("Token",token);
                                    session.editPreferences(token,username.getText().toString(),password.getText().toString(),"");
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getBaseContext(),"Terjadi kesalahan, coba cek koneksi anda",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void setAction() {
        (new EditText_Privilege(username,"Tidak boleh kosong")).setPrivilege();
        (new EditText_Privilege(password,"Tidak boleh kosong")).setPrivilege();
        sign_up.setOnClickListener(op);
        view.setOnClickListener(op);
        unview.setOnClickListener(op);
        login.setOnClickListener(op);
    }

    private void openIntent(Class page){
        Intent openPage = new Intent(getBaseContext(),page);
        //openPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //openPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(openPage);
    }
}
