package com.example.room_iot.types;

import android.content.Context;

import com.example.room_iot.supports.Memories;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AppCondition {
    private String type;
    public static final String MODULE = "module";
    public static final String TIME = "time";
    private String moduleId;
    private String moduleType;
    private int hour, minute;
    private boolean[] days = {false,false,false,false,false,false,false};
    private int minVal, maxVal;
    private boolean state;
    public boolean isOnce = true;
    public static final int AND = 2;
    public static final int OR = 1;
    private int blend = AND;
    public boolean isOn = true;

    public String getType() {
        return type;
    }

    public String getModuleId() {
        return moduleId;
    }

    public boolean evaluateCondition(Context context) throws JSONException {
        if(!isOn) return false;
        switch(type) {
            case TIME :
                return evaluateTime();
            case MODULE :
                return evaluateModule(context);
        }
        return false;
    }

    public boolean evaluateTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("kkmm");
        String nowTime = dateFormat.format(date);
        int nowHour = Integer.parseInt(nowTime.substring(0,2));
        int nowMin = Integer.parseInt(nowTime.substring(2,4));

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayWeek  = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if(isOnce || days[dayWeek]) {
            return nowHour == hour && nowMin == minute;
        }

        return false;
    }

    public boolean evaluateModule(Context context) throws JSONException {
        Memories memories = new Memories(context);
        Module module = memories.findModuleById(moduleId);
        if(module == null) return false;
        switch(moduleType) {
            case "onoff" :
                return state == module.getVal().equals("ON");
            case "slider" :
            case "value" :
                int val = memories.findModuleById(moduleId).getValInt();
                return (minVal <= val) && (maxVal >= val);
        }
        return false;
    }

    public void setModule(Module module) {
        moduleId = module.getModuleId();
        moduleType = module.getType();
    }

    public AppCondition(Module module, int fromVal, int toVal) {
        type = MODULE;
        if(fromVal < toVal) {
            minVal = fromVal;
            maxVal = toVal;
        }
        else {
            minVal = toVal;
            maxVal = fromVal;
        }
        setModule(module);
    }

    public AppCondition(Module module, boolean state) {
        type = MODULE;
        this.state = state;
        setModule(module);
    }

    public AppCondition(int hour, int minute, boolean[] days) {
        type = TIME;
        this.hour = hour;
        this.minute = minute;
        this.days = days;
        ifOnce();
    }

    public void setBlend(int blend) {
        this.blend = blend;
    }

    public int getBlend() {
        return blend;
    }

    private void ifOnce() {
        isOnce = true;
        for(int i = 0; i < 7; i++) if(days[i]) isOnce = false;
    }

    public JSONObject getJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        jsonObject.put("blend",blend);
        jsonObject.put("isOn", isOn);
        if(type.equals(MODULE)) {
            JSONObject moduleJSON = new JSONObject();
            moduleJSON.put("id", moduleId);
            moduleJSON.put("type", moduleType);
            if(moduleType.equals("onoff")) moduleJSON.put("state", state);
            else {
                moduleJSON.put("minVal", minVal);
                moduleJSON.put("maxVal", maxVal);
            }
            jsonObject.put("module", moduleJSON);
        }
        else {
            JSONObject timeJSON = new JSONObject();
            timeJSON.put("hour", hour);
            timeJSON.put("min", minute);
            JSONArray jsonArray = new JSONArray();
            for(int i = 0; i < 7; i++) {
                jsonArray.put(days[i]);
            }
            timeJSON.put("days", jsonArray);
            jsonObject.put("time", timeJSON);
        }
        return jsonObject;
    }

    public AppCondition(JSONObject jsonObject) throws JSONException {
        type = jsonObject.getString("type");
        blend = jsonObject.getInt("blend");
        isOn = jsonObject.getBoolean("isOn");
        if(type.equals(MODULE)) {
            JSONObject moduleJSON = jsonObject.getJSONObject("module");
            moduleId = moduleJSON.getString("id");
            moduleType = moduleJSON.getString("type");
            if(moduleType.equals("onoff")) state = moduleJSON.getBoolean("state");
            else {
                minVal = moduleJSON.getInt("minVal");
                maxVal = moduleJSON.getInt("maxVal");
            }
        }
        else {
            JSONObject timeJSON = jsonObject.getJSONObject("time");
            hour = timeJSON.getInt("hour");
            minute = timeJSON.getInt("min");

            JSONArray jsonArray = timeJSON.getJSONArray("days");
            for(int i = 0; i < 7; i++) {
                days[i] = jsonArray.getBoolean(i);
            }

            ifOnce();
        }
    }

    public String getText() {
        String str = "";
        if(type.equals(TIME)) {
            str += "is " + getTimeString();
            str += "\n" + getDaysString();
        }
        else {
            if(moduleType.equals("onoff")) str += "is " + (state? "ON" : "OFF");
            else str = "is greater than " + Integer.toString(minVal) + "\nand less than " + Integer.toString(maxVal);
        }
        return str;
    }

    private String getTimeString() {
        String str = "";
        int displayHour;
        displayHour = hour > 12? hour - 12 : hour;

        if(displayHour == 0) str += "12";
        else if(displayHour < 10) str += "0" + Integer.toString(displayHour);
        else str += Integer.toString(displayHour);

        str += ":";

        str += (minute < 10? "0" : "") + Integer.toString(minute) + (hour >= 12? " PM" : " AM");

        return str;
    }

    private String getDaysString() {
        String daysInNum = "0000000";
        String[] dayName = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        for(int i = 0; i < 7; i++) {
            if(days[i]) daysInNum = daysInNum.substring(0,i) + "1" + daysInNum.substring(i + 1);
        }
        if(daysInNum.equals("1111111")) return "Everyday";
        else if(daysInNum.equals("1000001")) return "Weekends";
        else if(daysInNum.equals("0111110")) return "Weekdays";
        else if(daysInNum.equals("0000000")) return "Once";
        char[] chars = daysInNum.toCharArray();
        String str = "";
        for(int i = 0; i < 7; i++) {
            if(chars[i] == '1') str += dayName[i] + ",";
        }
        str += "\b";
        return str;
    }
}
