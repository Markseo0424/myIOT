package com.example.room_iot.supports;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.room_iot.types.Module;
import com.example.room_iot.types.Schedule;
import com.example.room_iot.ui.schedules.ScheduleActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Memories {
    private Context context;
    private String fileName = "appData";
    public static final String MODULES = "modules";
    public static final String SCHEDULES = "schedules";
    public static final String SETTINGS = "settings";

    public Memories(Context context) {
        this.context = context;
    }

    public void clearData() {
        SharedPreferences pref = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    public void setData(String key, String dat) {
        SharedPreferences pref = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, dat);
        editor.commit();
    }

    public String getData(String key, String defaultString) {
        SharedPreferences pref = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return pref.getString(key, defaultString);
    }


    public JSONArray getObjectsJSON(String key) throws JSONException {
        String dataString = getData(key, "[]");
        JSONArray jsonArray = new JSONArray(dataString);
        return jsonArray;
    }

    public void setObjectsJSON(String key, JSONArray dat) {
        setData(key, dat.toString());
    }

    public void setModules(JSONArray modules) {
        setObjectsJSON(MODULES, modules);
    }

    public Module[] getModules() throws JSONException {
        JSONArray jsonArray = getObjectsJSON(MODULES);
        int len = jsonArray.length();
        Module[] res = new Module[len];
        for (int i = 0; i < len; i++) {
            res[i] = new Module((JSONObject) jsonArray.get(i));
        }
        return res;
    }

    public int findById(String id, JSONArray jsonArray) throws JSONException {
        for(int i = 0; i < jsonArray.length(); i++) {
            if(jsonArray.getJSONObject(i).getString("id").equals(id)) return i;
        }
        return -1;
    }

    public Module findModuleById(String id) throws JSONException {
        JSONArray jsonArray = getObjectsJSON(MODULES);
        int index = findById(id,jsonArray);
        if(index == -1) return null;
        return getModules()[index];
    }

    public int addModuleJSON(JSONObject module) throws JSONException {
        JSONArray jsonArray = getObjectsJSON(MODULES);
        if(findById(module.getString("id"), jsonArray) != -1) return 1;
        jsonArray.put(module);
        setObjectsJSON(MODULES, jsonArray);
        return 0;
    }

    public void delModule(String id) throws JSONException {
        JSONArray jsonArray = getObjectsJSON(MODULES);
        int index = findById(id,jsonArray);
        if(index == -1) return;
        jsonArray.remove(index);
        setObjectsJSON(MODULES, jsonArray);
    }

    public int addModule(Module module) throws JSONException {
        return addModuleJSON(module.getJson());
    }

    public void editVal(String id, String val) throws JSONException {
        JSONArray jsonArray = getObjectsJSON(MODULES);
        int index = findById(id,jsonArray);
        if(index == -1) return;
        JSONObject jsonObject = jsonArray.getJSONObject(index);
        jsonObject.put("val",val);
        jsonArray.put(index,jsonObject);
        setObjectsJSON(MODULES,jsonArray);
    }

    public void editName(String id, String name) throws JSONException {
        JSONArray jsonArray = getObjectsJSON(MODULES);
        int index = findById(id,jsonArray);
        if(index == -1) return;
        JSONObject jsonObject = jsonArray.getJSONObject(index);
        jsonObject.put("name",name);
        jsonArray.put(index,jsonObject);
        setObjectsJSON(MODULES,jsonArray);
    }

    public int findSchedule(String name) throws JSONException {
        JSONArray schedules = getObjectsJSON(SCHEDULES);
        for(int i = 0; i < schedules.length(); i++) {
            if(schedules.getJSONObject(i).getString("name").equals(name)) return i;
        }
        return -1;
    }

    public boolean addSchedule(Schedule schedule) throws JSONException {
        if(findSchedule(schedule.getName()) != -1) return false;
        JSONArray schedules = getObjectsJSON(SCHEDULES);
        schedules.put(schedule.getJson());
        setObjectsJSON(SCHEDULES,schedules);
        return true;
    }

    public boolean editSchedule(Schedule schedule, int index) throws JSONException {
        if(findSchedule(schedule.getName()) != -1 && findSchedule(schedule.getName()) != index) return false;
        JSONArray schedules = getObjectsJSON(SCHEDULES);
        schedules.put(index, schedule.getJson());
        setObjectsJSON(SCHEDULES,schedules);
        return true;
    }

    public boolean removeSchedule(String name) throws JSONException {
        int index = findSchedule(name);
        if(index == -1) return false;
        JSONArray schedules = getObjectsJSON(SCHEDULES);
        schedules.remove(index);
        setObjectsJSON(SCHEDULES,schedules);
        return true;
    }

    public JSONObject findScheduleJSON(String name) throws JSONException {
        int index = findSchedule(name);
        if(index == -1) return null;
        return getObjectsJSON(SCHEDULES).getJSONObject(index);
    }

    public Schedule getSchedule(String name) throws JSONException {
        return new Schedule(findScheduleJSON(name));
    }

    public Schedule[] getSchedules() throws JSONException {
        JSONArray jsonArray = getObjectsJSON(SCHEDULES);
        Schedule[] schedules = new Schedule[jsonArray.length()];
        for(int i = 0; i < jsonArray.length(); i++) {
            schedules[i] = new Schedule(jsonArray.getJSONObject(i));
        }
        return schedules;
    }

    public void editIsOn(String name, boolean isOn) throws JSONException {
        JSONArray schedules = getObjectsJSON(SCHEDULES);
        int index = findSchedule(name);
        JSONObject schedule = schedules.getJSONObject(index);
        schedule.put("isOn", isOn);
        schedules.put(index,schedule);
        setObjectsJSON(SCHEDULES,schedules);
    }
}
