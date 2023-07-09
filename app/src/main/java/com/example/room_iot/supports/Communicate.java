package com.example.room_iot.supports;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.room_iot.types.AppRequest;
import com.example.room_iot.types.AppResponse;
import com.example.room_iot.caller.AppResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

public class Communicate {
    public static String url;
    private final String defaultUrl = "http://192.168.219.102:3000";
    Context context;
    Memories memories;

    public Communicate(Context context) {
        this.context = context;
        memories = new Memories(context);
        url = memories.getData("url","http://192.168.219.102:3000");
    }
    public void sendRequest(AppRequest appRequest, AppResponseListener appResponseListener) throws JSONException {
        JSONObject sendJson = appRequest.getJson();
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, sendJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        AppResponse appResponse = new AppResponse(response);
                        try {
                            appResponseListener.onResponse(appResponse);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        appResponseListener.onError(error);
                        //Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,0 * DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
    }

    public void checkConnection(String url, AppResponseListener appResponseListener) throws JSONException {
        JSONObject sendJson = new JSONObject();
        sendJson.put("requestId", "VALID");
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, sendJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast.makeText(context, "Received", Toast.LENGTH_SHORT).show();
                        AppResponse appResponse = new AppResponse(response);
                        //Toast.makeText(context, "Response made", Toast.LENGTH_SHORT).show();
                        try {
                            appResponseListener.onResponse(appResponse);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        appResponseListener.onError(error);
                        //Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
    }
}
