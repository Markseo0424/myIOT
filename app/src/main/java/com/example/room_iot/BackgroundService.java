package com.example.room_iot;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.VolleyError;
import com.example.room_iot.caller.AppCaller;
import com.example.room_iot.caller.AppResponseListener;
import com.example.room_iot.caller.EndServiceListener;
import com.example.room_iot.supports.AlertPrint;
import com.example.room_iot.supports.Communicate;
import com.example.room_iot.supports.Memories;
import com.example.room_iot.types.AppRequest;
import com.example.room_iot.types.AppResponse;
import com.example.room_iot.types.Module;
import com.example.room_iot.types.Schedule;
import com.example.room_iot.ui.modules.ModulesFragment;

import org.json.JSONException;

import java.security.Provider;
import java.util.Arrays;

public class BackgroundService extends Service {
    public static final String CHANNEL_ID = "room_iot";
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    int loopDelayTime = 15 * 2000;

    Memories memories;
    Communicate communicate;

    boolean isRunning = false;

    private final class ServiceHandler extends Handler implements EndServiceListener {
        private boolean loopOn = true;

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            while(loopOn) {
                try {
                    communicate.checkConnection(Communicate.url, new AppResponseListener() {
                        @Override
                        public void onResponse(AppResponse appResponse) throws JSONException {
                            try {Sync();} catch (JSONException ignored) {}
                            Schedule[] schedules = memories.getSchedules();
                            for(int i = 0; i < schedules.length; i++) {
                                Schedule schedule = schedules[i];
                                if(schedule.isOn() && schedule.evaluateConditions(BackgroundService.this)) schedule.doActions(BackgroundService.this);
                                memories.editSchedule(schedule, i);
                            }
                            if(appResponse.isOK() && !isRunning) {
                                setNotification("Sync is running");
                                isRunning = true;
                            }
                        }

                        @Override
                        public void onError(VolleyError error) {
                            if(isRunning) {
                                setNotification("Server not connected");
                                isRunning = false;
                            }
                        }
                    });
                } catch (JSONException ignored) {}
                try {Thread.sleep(loopDelayTime);} catch (InterruptedException ignored) {}
            }


        }

        @Override
        public void onEndService() {
            loopOn = false;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        memories = new Memories(this);
        communicate = new Communicate(this);
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        Notification notification = setNotification("Server not connected");
        startForeground(1, notification);

        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        createNotificationChannel("END");
        setNotification("Service is stopped", "END");
        serviceHandler.onEndService();
    }

    private void createNotificationChannel() {
        createNotificationChannel(CHANNEL_ID);
    }

    private void createNotificationChannel(String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Foreground running";
            String description = "Foreground running notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public Notification setNotification(String text) {
        return setNotification(text,CHANNEL_ID);
    }

    public Notification setNotification(String text, String channelId) {
        Intent notificationIntent = new Intent(this, MainActivity.class)
                .setAction(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LAUNCHER)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent =  PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.small_icon)
                .setContentTitle("myIOT")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, notification);
        return notification;
    }

    public void Sync() throws JSONException {
        communicate.sendRequest(new AppRequest(), new AppResponseListener() {
            @Override
            public void onResponse(AppResponse appResponse) throws JSONException {
                Module[] moduleList = memories.getModules();
                for (Module module : moduleList) {
                    String id = module.getModuleId();
                    memories.editVal(id, appResponse.getVal(id));
                }
                AppCaller.callSyncListener();
            }
        });
    }
}