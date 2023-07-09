package com.example.room_iot.ui.schedules.condition.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.room_iot.ui.schedules.condition.ConditionModuleFragment;
import com.example.room_iot.ui.schedules.condition.ConditionTimeFragment;

public class ConditionViewPagerAdapter extends FragmentStateAdapter {
    private final int NUM_TAB = 2;

    public ConditionViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0 : return new ConditionModuleFragment();
            case 1 : return new ConditionTimeFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return NUM_TAB;
    }
}
