package com.example.room_iot.ui.modules;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.room_iot.supports.Communicate;
import com.example.room_iot.supports.Memories;
import com.example.room_iot.R;
import com.example.room_iot.caller.AppCaller;
import com.example.room_iot.caller.SyncListener;
import com.example.room_iot.types.AppRequest;
import com.example.room_iot.caller.AppResponseListener;
import com.example.room_iot.types.Module;

import org.json.JSONException;




public class ModuleAdapter extends ArrayAdapter<Module>  {
    Memories memories;
    Communicate communicate;

    public ModuleAdapter(@NonNull Context context, @NonNull Module[] objects) {
        super(context, R.layout.adapter_module_onoff, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        memories = new Memories(getContext());
        communicate = new Communicate(getContext());

        Module item = getItem(position);
        LayoutInflater imageInflater = LayoutInflater.from(getContext());
        View view = imageInflater.inflate(R.layout.adapter_module_value, parent,false);

        try {
            switch (item.getType()) {
                case "onoff":
                    view = imageInflater.inflate(R.layout.adapter_module_onoff, parent, false);
                    Switch moduleSwitch = view.findViewById(R.id.moduleSwitch);
                    moduleSwitch.setThumbTintList(getContext().getResources().getColorStateList(R.color.switch_thumb_set_1,null));
                    moduleSwitch.setTrackTintList(getContext().getResources().getColorStateList(R.color.switch_track_set_1,null));
                    moduleSwitch.setFocusable(false);
                    moduleSwitch.setFocusableInTouchMode(false);
                    boolean isChecked = item.getVal().equals("ON");
                    moduleSwitch.setChecked(isChecked);
                    AppCaller.setSyncListener(new SyncListener() {
                        @Override
                        public void onSync() throws JSONException {
                            boolean isChecked = memories.findModuleById(item.getModuleId()).getVal().equals("ON");
                            AppCaller.switchListenOn = false;
                            moduleSwitch.setChecked(isChecked);
                            AppCaller.switchListenOn = true;
                        }
                    });
                    moduleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if(!AppCaller.switchListenOn) return;
                            try {
                                String id = item.getModuleId();
                                String val = b?"ON":"OFF";
                                memories.editVal(id,val);
                                communicate.sendRequest(new AppRequest(id,val),new AppResponseListener());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
                case "slider":
                    view = imageInflater.inflate(R.layout.adapter_module_slider, parent, false);
                    TextView valueText = (TextView) view.findViewById(R.id.value);
                    SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar);
                    seekBar.setFocusable(false);
                    seekBar.setFocusableInTouchMode(false);
                    AppCaller.setSyncListener(new SyncListener() {
                        @Override
                        public void onSync() throws JSONException {
                            int value = Integer.parseInt(memories.findModuleById(item.getModuleId()).getVal());
                            valueText.setText(Integer.toString(value));
                            seekBar.setProgress(value);
                        }
                    });
                    try {
                        int value = Integer.parseInt(item.getVal());
                        valueText.setText(Integer.toString(value));
                        seekBar.setProgress(value);
                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar1, int i, boolean b) {
                                valueText.setText(Integer.toString(i));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar1) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar1) {
                                try {
                                    String id = item.getModuleId();
                                    String val = Integer.toString(seekBar.getProgress());
                                    memories.editVal(id,val);
                                    communicate.sendRequest(new AppRequest(id,val),new AppResponseListener());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "value":
                    view = imageInflater.inflate(R.layout.adapter_module_value, parent, false);
                    TextView valueText2 = (TextView) view.findViewById(R.id.value);
                    AppCaller.setSyncListener(new SyncListener() {
                        @Override
                        public void onSync() throws JSONException {
                            valueText2.setText(memories.findModuleById(item.getModuleId()).getVal());
                        }
                    });
                    try {
                        valueText2.setText(item.getVal());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + item.getType());
            }
        }catch(Exception e) {
            e.printStackTrace();
        }


        TextView nameText = (TextView) view.findViewById(R.id.moduleName);
        TextView idText = (TextView) view.findViewById(R.id.moduleId);


        try {
            nameText.setText(item.getModuleName());
            idText.setText(item.getModuleId());
        } catch (Exception e) {
            e.printStackTrace();
        }


        return view;
    }
}
