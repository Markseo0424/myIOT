package com.example.room_iot.ui.schedules;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.room_iot.supports.Memories;
import com.example.room_iot.databinding.FragmentSchedulesBinding;
import com.example.room_iot.types.Schedule;

import org.json.JSONException;

public class SchedulesFragment extends Fragment {
    Memories memories;
    private FragmentSchedulesBinding binding;
    Schedule[] schedules;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSchedulesBinding.inflate(inflater, container, false);
        memories = new Memories(getContext());

        View root = binding.getRoot();

        binding.fab2.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ScheduleActivity.class);
            intent.putExtra("mode", "add");
            startActivity(intent);
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            schedules = memories.getSchedules();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ListAdapter listAdapter = new ScheduleAdapter(getActivity(), schedules);
        binding.scheduleList.setAdapter(listAdapter);
        binding.scheduleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                intent.putExtra("mode", "edit");
                intent.putExtra("name", schedules[i].getName());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}