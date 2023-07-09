package com.example.room_iot.ui.schedules.condition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.room_iot.supports.Memories;
import com.example.room_iot.databinding.ActivityConditionBinding;
import com.example.room_iot.types.Module;
import com.example.room_iot.ui.schedules.condition.adapter.ConditionViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;

public class ConditionActivity extends AppCompatActivity {
    ActivityConditionBinding binding;
    Memories memories;
    Module[] modules;

    String[] tabName = {"module", "time"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConditionBinding.inflate(getLayoutInflater());
        memories = new Memories(this);
        try {
            modules = memories.getModules();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        binding.viewPager.setAdapter(new ConditionViewPagerAdapter(this));
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(binding.tabLayout, binding.viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabName[position]);
            }
        });
        tabLayoutMediator.attach();

        setContentView(binding.getRoot());
    }
}