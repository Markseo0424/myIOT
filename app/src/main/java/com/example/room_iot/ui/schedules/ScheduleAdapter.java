package com.example.room_iot.ui.schedules;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.room_iot.caller.AppCaller;
import com.example.room_iot.caller.SyncListener;
import com.example.room_iot.supports.Communicate;
import com.example.room_iot.supports.Memories;
import com.example.room_iot.R;
import com.example.room_iot.types.Schedule;

import org.json.JSONException;


public class ScheduleAdapter extends ArrayAdapter<Schedule>  {
    Memories memories;
    Communicate communicate;

    public ScheduleAdapter(@NonNull Context context, @NonNull Schedule[] objects) {
        super(context, R.layout.adapter_schedule, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        memories = new Memories(getContext());
        communicate = new Communicate(getContext());

        Schedule item = getItem(position);
        LayoutInflater imageInflater = LayoutInflater.from(getContext());
        View view = imageInflater.inflate(R.layout.adapter_schedule, parent,false);

        Switch scheduleSwitch = view.findViewById(R.id.scheduleSwitch);
        TextView nameText = (TextView) view.findViewById(R.id.scheduleName);

        Button button = (Button) view.findViewById(R.id.button);
        button.setFocusable(false);
        button.setFocusableInTouchMode(false);
        button.setOnClickListener(view1 ->{
            try {
                Toast.makeText(getContext(),String.valueOf(item.evaluateConditions(getContext())), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
/*
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(item.getJson().toString());
                item.evaluateConditions(getContext());
                builder.setTitle(Boolean.toString(item.evaluateConditions(getContext())))
                        .setCancelable(false)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

                if(item.evaluateConditions(getContext())) item.doActions(getContext());


            } catch (JSONException e) {
                e.printStackTrace();
            }

 */

        });

        scheduleSwitch.setFocusable(false);
        scheduleSwitch.setFocusableInTouchMode(false);
        scheduleSwitch.setChecked(item.isOn());
        scheduleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                try {
                    memories.editIsOn(item.getName(), b);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        AppCaller.setSyncListener(new SyncListener() {
            @Override
            public void onSync() throws JSONException {
                scheduleSwitch.setChecked(memories.getSchedule(item.getName()).isOn());
            }
        });

        try {
            nameText.setText(item.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }


        return view;
    }
}
