package com.example.room_iot.types;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
    List<AppCondition> conditions = new ArrayList<>();
    List<AppAction> actions = new ArrayList<>();
    boolean isOn = true;
    String name;

    public List<AppCondition> getConditions() {
        return conditions;
    }

    public List<AppAction> getActions() {
        return actions;
    }

    public boolean isOn() {
        return isOn;
    }

    public String getName() {
        return name;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public void setName(String name) {
        this.name = name;
    }

    public interface Sleeper {
        void Sleep(int millis) throws InterruptedException;
    }

    public Schedule(String name, List<AppCondition> conditions, List<AppAction> actions) {
        this.name = name;
        this.conditions = conditions;
        this.actions = actions;
    }

    public boolean evaluateConditions(Context context) throws JSONException {
        if(!isOn) return false;

        boolean skip = false;
        boolean state = false;
        boolean inAnd = false;
        int timeCondition = -1;

        for(int i = 0; i < conditions.size(); i++) {
            AppCondition condition = conditions.get(i);

            if(skip && condition.getBlend() == AppCondition.AND) {
                if (!condition.getType().equals(AppCondition.TIME)) continue;
            }
            else skip = false;

            if(condition.evaluateCondition(context)) { //if condition is true
                if(condition.getType().equals(AppCondition.TIME)){
                    timeCondition = i;
                }

                if(condition.getBlend() == AppCondition.AND) {
                    if(!inAnd) {
                        state = true;
                        inAnd = true;
                    }
                }
                else {
                    if(inAnd && !state) {
                        timeCondition = -1;
                        inAnd = false;
                        continue;
                    }
                    state = true;
                    break;
                }
            }
            else {//if condition is false
                if(condition.getType().equals(AppCondition.TIME)){
                    if(!condition.evaluateTime()) conditions.get(i).isOn = true;
                }

                if(condition.getBlend() == AppCondition.AND) {
                    state = false;
                    skip = true;
                    if(!inAnd) inAnd = true;
                }
                else {
                    timeCondition = -1;
                    inAnd = false;
                    state = false;
                }
            }
        }

        if(state && timeCondition != -1) {
            conditions.get(timeCondition).isOn = false;
            /*
            int finalTimeCondition = timeCondition;
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    conditions.get(finalTimeCondition).isOn = true;
                }
            },60000);
             */
            if(conditions.get(timeCondition).isOnce) {
                isOn = false;
            }
        }

        return state;
    }

    public void doActions(Context context) throws JSONException {
        int totalDelayTime = 0;
        for(int i = 0; i < actions.size(); i++) {
            AppAction action = actions.get(i);
            int finalI = i;
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        action.doAction(context);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },totalDelayTime);
            totalDelayTime += action.getDelayTime()*1000;
        }
    }

    public JSONObject getJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray conditionArray = new JSONArray();
        JSONArray actionArray = new JSONArray();

        jsonObject.put("name",name);
        jsonObject.put("isOn", isOn);
        for(int i = 0; i < conditions.size(); i++) conditionArray.put(conditions.get(i).getJson());
        for(int i = 0; i < actions.size(); i++) actionArray.put(actions.get(i).getJson());
        jsonObject.put("conditions", conditionArray);
        jsonObject.put("actions", actionArray);
        return jsonObject;
    }

    public Schedule(JSONObject jsonObject) throws JSONException {
        JSONArray conditionArray = jsonObject.getJSONArray("conditions");
        JSONArray actionArray = jsonObject.getJSONArray("actions");
        for(int i = 0; i < conditionArray.length(); i++) conditions.add(new AppCondition(conditionArray.getJSONObject(i)));
        for(int i = 0; i < actionArray.length(); i++) actions.add(new AppAction(actionArray.getJSONObject(i)));
        name = jsonObject.getString("name");
        isOn = jsonObject.getBoolean("isOn");
    }
}
