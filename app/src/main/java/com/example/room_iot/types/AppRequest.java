package com.example.room_iot.types;

import org.json.JSONException;
import org.json.JSONObject;

public class AppRequest {
    final static public int NEW = 0;
    final static public int SEARCH = 1;
    final static public int DEL = 2;

    private String requestID;
    private String moduleId;
    private String reqVal;

    public AppRequest() {
        requestID = "LIST";
    }

    public AppRequest(String id, int mode) {
        switch(mode) {
            case NEW : requestID = "NEW";
            break;
            case SEARCH : requestID = "SEARCH";
            break;
            case DEL : requestID = "DEL";
            break;
            default:
                throw new IllegalStateException("Unexpected value: " + mode);
        }
        moduleId = id;
    }

    public AppRequest(String id, String reqVal) {
        requestID = "VAL";
        moduleId = id;
        this.reqVal = reqVal;
    }

    public JSONObject getJson() throws JSONException {
        JSONObject result = new JSONObject();
        JSONObject dat = new JSONObject();

        switch(requestID) {
            case "LIST":
                break;
            case "VAL":
                dat.put("reqVal", reqVal);
            case "NEW":
            case "SEARCH":
            case"DEL":
                dat.put("id",moduleId);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestID);
        }
        result.put("requestId", requestID);
        result.put("data", dat);

        return result;
    }
}
