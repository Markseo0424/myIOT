package com.example.room_iot.ui.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.room_iot.R;
import com.example.room_iot.databinding.ActivitySettingBinding;

public class SettingActivity extends AppCompatActivity {
    ActivitySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_setting);
    }
}