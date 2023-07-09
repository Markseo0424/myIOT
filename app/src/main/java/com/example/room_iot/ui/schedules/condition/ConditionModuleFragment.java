package com.example.room_iot.ui.schedules.condition;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.room_iot.supports.Memories;
import com.example.room_iot.databinding.FragmentConditionModuleBinding;
import com.example.room_iot.types.AppCondition;
import com.example.room_iot.types.Module;
import com.example.room_iot.ui.schedules.SelectModuleAdapter;

import org.json.JSONException;

public class ConditionModuleFragment extends Fragment {
    FragmentConditionModuleBinding binding;
    Memories memories;
    Module[] modules;

    @Nullable
    Module selectedModule = null;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentConditionModuleBinding.inflate(inflater, container, false);
        memories = new Memories(getActivity());

        try {
            modules = memories.getModules();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        View root = binding.getRoot();

        binding.selectButton.setOnClickListener(view -> {
            if(binding.moduleListView.getVisibility() == View.GONE) binding.moduleListView.setVisibility(View.VISIBLE);
            else binding.moduleListView.setVisibility(View.GONE);
        });

        ListAdapter listAdapter = new SelectModuleAdapter(getActivity(), modules);
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
                    setValueVisible(View.GONE);
                    break;
                case "slider" :
                    setSwitchVisible(View.GONE);
                    setSliderVisible(View.VISIBLE);
                    setValueVisible(View.GONE);
                    break;
                case "value" :
                    setSwitchVisible(View.GONE);
                    setSliderVisible(View.GONE);
                    setValueVisible(View.VISIBLE);
                    break;
            }
            binding.moduleListView.setVisibility(View.GONE);
        });

        binding.addButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            switch(selectedModule.getType()) {
                case "onoff" :
                    try {
                        intent.putExtra("condition",
                                (new AppCondition(selectedModule,binding.switch1.isChecked()
                                )).getJson().toString());
                    } catch (JSONException e) {e.printStackTrace();}
                    break;
                case "slider" :
                    try {
                        intent.putExtra("condition",
                                (new AppCondition(selectedModule,
                                        binding.fromSeekBar.getProgress(),
                                        binding.toSeekBar.getProgress()
                                )).getJson().toString()
                        );
                    } catch (JSONException e) {e.printStackTrace();}
                    break;
                case "value" :
                    try {
                        intent.putExtra("condition",
                                (new AppCondition(selectedModule,
                                        Integer.parseInt(binding.fromVal.getText().toString()),
                                        Integer.parseInt(binding.toVal.getText().toString())
                                )).getJson().toString()
                        );
                    } catch (JSONException e) {e.printStackTrace();}
                    break;
            }
            getActivity().setResult(301,intent);
            getActivity().finish();
        });

        binding.fromSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                binding.fromNum.setText(Integer.toString(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.toSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                binding.toNum.setText(Integer.toString(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return root;
    }

    private void setSwitchVisible(int state) {
        binding.switchText.setVisibility(state);
        binding.switch1.setVisibility(state);
    }

    private void setSliderVisible(int state) {
        binding.sliderText.setVisibility(state);
        binding.fromText.setVisibility(state);
        binding.toText.setVisibility(state);
        binding.fromSeekBar.setVisibility(state);
        binding.toSeekBar.setVisibility(state);
        binding.fromNum.setVisibility(state);
        binding.toNum.setVisibility(state);
    }

    private void setValueVisible(int state) {
        binding.valueText.setVisibility(state);
        binding.fromText2.setVisibility(state);
        binding.toText2.setVisibility(state);
        binding.fromVal.setVisibility(state);
        binding.toVal.setVisibility(state);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
