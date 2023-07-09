package com.example.room_iot.types;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AppResponse {
    private String responseID;

    @Nullable
    private JSONObject dat;
    @Nullable
    private JSONArray listDat;

    public AppResponse(JSONObject res) {
        try {
            responseID = res.getString("responseId");
            if(responseID.equals("LIST")) listDat = res.getJSONArray("data");
            else dat = res.getJSONObject("data");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getResponseID() {
        return responseID;
    }

    @Nullable
    public String getID() throws JSONException {
        if(dat == null) return null;
        return dat.getString("id");
    }

    public boolean isOK() throws JSONException {
        return dat.getString("result").equals("OK");
    }

    public String getType() throws JSONException {
        return dat.getString("type");
    }

    public String getVal() throws JSONException {
        return dat.getString("val");
    }

    public Module getModule() throws JSONException {
        return new Module(dat);
    }

    public Module[] getModules() throws JSONException {
        int len = listDat.length();
        Module[] res = new Module[len];
        for(int i = 0; i < len; i++) {
            res[i] = new Module((JSONObject) listDat.get(i));
        }
        return res;
    }

    public JSONObject getDat() {
        return dat;
    }

    public JSONArray getListDat() {
        return listDat;
    }

    @Nullable
    public String getVal(String id) throws JSONException {
        int len = listDat.length();
        for(int i = 0; i < len; i++) {
            if(id.equals(listDat.getJSONObject(i).getString("id"))) return listDat.getJSONObject(i).getString("val");
        }
        return null;
    }

    public String getString() {
        return listDat.toString();
    }
}
