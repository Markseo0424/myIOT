package com.example.room_iot.types;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class Module {
    @Nullable
    private String moduleName;
    @Nullable
    private String moduleId;
    @Nullable
    private String type;
    @Nullable
    private String val;

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setVal(String val) {
        this.val = val;
    }

    @Nullable
    public String getModuleName() {
        return moduleName;
    }

    @Nullable
    public String getModuleId() {
        return moduleId;
    }

    @Nullable
    public String getType() {
        return type;
    }

    @Nullable
    public String getVal() {
        return val;
    }

    public int getValInt() {
        try {
            return Integer.parseInt(val);
        }catch(Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public Module(@NonNull JSONObject jsonObject) throws JSONException {
        if(!jsonObject.isNull("name")) moduleName = jsonObject.getString("name");
        moduleId = jsonObject.getString("id");
        type = jsonObject.getString("type");
        val = jsonObject.getString("val");
    }

    public Module(String name) {
        moduleName = name;
    }

    public Module(){

    }

    public JSONObject getJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        if(moduleName != null && !moduleName.equals("")) jsonObject.put("name", moduleName);
        if(moduleId != null && !moduleId.equals("")) jsonObject.put("id", moduleId);
        if(type != null && !type.equals("")) jsonObject.put("type", type);
        if(val != null && !val.equals("")) jsonObject.put("val", val);
        return jsonObject;
    }

    public String getJsonString() throws JSONException {
        return this.getJson().toString();
    }
}
