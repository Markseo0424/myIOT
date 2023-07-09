package com.example.room_iot.types;

import android.content.Context;

import com.example.room_iot.caller.AppResponseListener;
import com.example.room_iot.supports.Communicate;
import com.example.room_iot.supports.Memories;

import org.json.JSONException;
import org.json.JSONObject;

public class AppAction {
    String moduleId;
    String val;
    int delayTime = 0;

    public AppAction(String moduleId, String val){
        this.moduleId = moduleId;
        this.val = val;
    }

    public AppAction(Module module, String val){
        this.moduleId = module.getModuleId();
        this.val = val;
    }

    public void doAction(Context context) throws JSONException {
        Memories memories = new Memories(context);
        Communicate communicate = new Communicate(context);
        Module module = memories.findModuleById(moduleId);
        if(module == null) return;
        if(module.getVal().equals(val)) return;
        communicate.sendRequest(new AppRequest(moduleId, val), new AppResponseListener());
        memories.editVal(moduleId, val);
    }

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    public JSONObject getJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", moduleId);
        jsonObject.put("val", val);
        jsonObject.put("delay", delayTime);
        return jsonObject;
    }

    public AppAction(JSONObject jsonObject) throws JSONException {
        moduleId = jsonObject.getString("id");
        val = jsonObject.getString("val");
        delayTime = jsonObject.getInt("delay");
    }

    public String getModuleId() {
        return moduleId;
    }

    public String getText() {
        return "to " + val;
    }
}
