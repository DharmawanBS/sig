package com.dharmawan.myapplication;

import com.android.volley.VolleyError;

/**
 * Created by dharmawan on 12/3/2017.
 */

public interface Callback {
    void onSuccessResponse(String result);
    void onErrorResponse(VolleyError error);
}
