package com.example.room_iot.caller;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AppCaller {
    public static boolean switchListenOn = true;
    private static final List<SyncListener> syncListeners = new ArrayList<>();

    public static void setSyncListener(SyncListener syncListener) {
        syncListeners.add(syncListener);
    }

    public static void callSyncListener() throws JSONException {
        for(int i = syncListeners.size() - 1; i >= 0; i--) {
            try{syncListeners.get(i).onSync();} catch(Exception e) {syncListeners.remove(i);}
        }
    }
}
