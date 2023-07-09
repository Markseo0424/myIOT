package com.example.room_iot.caller;

import com.android.volley.VolleyError;
import com.example.room_iot.types.AppResponse;

import org.json.JSONException;

public class AppResponseListener {
    public void onResponse(AppResponse appResponse) throws JSONException {
        return;
    }

    public void onError(VolleyError error) {
        return;
    }
}
