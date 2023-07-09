package com.example.room_iot.ui.schedules.action;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.room_iot.supports.Memories;
import com.example.room_iot.databinding.ActivityActionBinding;
import com.example.room_iot.types.AppAction;
import com.example.room_iot.types.Module;
import com.example.room_iot.ui.schedules.SelectModuleAdapter;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ActionActivity extends AppCompatActivity {
    ActivityActionBinding binding;
    Memories memories;
    Module[] modules;
    List<Module> actionModules = new ArrayList<>();

    @Nullable
    Module selectedModule = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityActionBinding.inflate(getLayoutInflater());
        memories = new Memories(this);
        setContentView(binding.getRoot());

        try {
            modules = memories.getModules();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < modules.length; i++) {
            if(!modules[i].getType().equals("value")) actionModules.add(modules[i]);
        }
        modules = new Module[actionModules.size()];
        modules = actionModules.toArray(modules);

        binding.selectButton.setOnClickListener(view -> {
            if(binding.moduleListView.getVisibility() == View.GONE) binding.moduleListView.setVisibility(View.VISIBLE);
            else binding.moduleListView.setVisibility(View.GONE);
        });

        ListAdapter listAdapter = new SelectModuleAdapter(this, modules);
        binding.moduleListView.setAdapter(listAdapter);

        binding.moduleListView.setOnItemClickListener((adapterView, view, i, l) -> {
            selectedModule = modules[i];
            binding.selectButton.setText(selectedModule.getModuleName());
            binding.idText.setText(selectedModule.getModuleId());
            binding.typeText.setText(selectedModule.getType());
            binding.addButton.setEnabled(true);

            switch(selectedModule.getType()) {
                case "onoff" :
                    setSwitchVisible(View.VISIBLE);
                    setSliderVisible(View.GONE);
                    break;
                case "slider" :
                    setSwitchVisible(View.GONE);
                    setSliderVisible(View.VISIBLE);
                    break;
            }
            binding.moduleListView.setVisibility(View.GONE);
        });

        binding.addButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            switch(selectedModule.getType()) {
                case "onoff" :
                    try {
                        intent.putExtra("action",
                                (new AppAction(selectedModule,binding.switch1.isChecked()?"ON":"OFF"
                                )).getJson().toString());
                    } catch (JSONException e) {e.printStackTrace();}
                    break;
                case "slider" :
                    try {
                        intent.putExtra("action",
                                (new AppAction(selectedModule,
                                        Integer.toString(binding.setSeekBar.getProgress())
                                )).getJson().toString()
                        );
                    } catch (JSONException e) {e.printStackTrace();}
                    break;
            }
            setResult(302,intent);
            finish();
        });

        binding.setSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                binding.setNum.setText(Integer.toString(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void setSwitchVisible(int state) {
        binding.switchText.setVisibility(state);
        binding.switch1.setVisibility(state);
    }

    private void setSliderVisible(int state) {
        binding.sliderText.setVisibility(state);
        binding.textView333.setVisibility(state);
        binding.setSeekBar.setVisibility(state);
        binding.setNum.setVisibility(state);
    }
}
