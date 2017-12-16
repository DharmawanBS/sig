package com.dharmawan.myapplication;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by dharmawan on 12/6/17.
 */

public class EditText_Privilege {
    EditText et;
    String error_msg;

    public EditText_Privilege(EditText et, String error_msg) {
        this.et = et;
        this.error_msg = error_msg;
    }

    public void setPrivilege() {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (et.getText().toString().trim().length() == 0) {
                    et.setError(error_msg);
                } else {
                    et.setError(null);
                }
            }
        });
    }
}
