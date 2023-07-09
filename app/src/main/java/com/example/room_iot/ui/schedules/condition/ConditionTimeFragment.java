package com.example.room_iot.ui.schedules.condition;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.room_iot.R;
import com.example.room_iot.databinding.FragmentConditionTimeBinding;
import com.example.room_iot.types.AppCondition;

import org.json.JSONException;

public class ConditionTimeFragment extends Fragment {
    FragmentConditionTimeBinding binding;
    boolean[] days = {false,false,false,false,false,false,false};
    ToggleButton[] daysButton;
    TextView[] daysText;

    int hour = 0;
    int minute = 0;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentConditionTimeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        daysButton = new ToggleButton[]{binding.sunButton,binding.monButton,binding.tueButton,binding.wedButton,binding.thuButton,binding.friButton,binding.satButton};
        daysText = new TextView[]{binding.sunText,binding.monText,binding.tueText,binding.wedText,binding.thuText,binding.friText,binding.satText};

        for(int i = 0; i < 7; i++) {
            int finalI = i;
            daysButton[i].setOnCheckedChangeListener((compoundButton, b) -> {
                daysText[finalI].setTextColor(getResources().getColorStateList(b ? R.color.white : R.color.theme_primary, null));
                days[finalI] = b;
            });
        }

        binding.timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog dialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hour = i;
                        minute = i1;
                        onStart();
                    }
                }, hour, minute, false);
                dialog.show();
            }
        });

        binding.addButton2.setOnClickListener(view -> {
            Intent intent = new Intent();
            try {
                intent.putExtra("condition", (new AppCondition(hour,minute,days)).getJson().toString());
            } catch (JSONException e) {e.printStackTrace();}
            getActivity().setResult(301,intent);
            getActivity().finish();
        });

        return root;
    }


    @Override
    public void onStart() {
        binding.timeText.setText(getTimeString());
        binding.textView30.setText(hour >= 12? "PM" : "AM");
        super.onStart();
    }

    private String getTimeString() {
        String str = "";
        int displayHour;
        displayHour = hour > 12? hour - 12 : hour;

        if(displayHour == 0) str += "12";
        else if(displayHour < 10) str += "0" + Integer.toString(displayHour);
        else str += Integer.toString(displayHour);

        str += ":";

        str += (minute < 10? "0" : "") + Integer.toString(minute) + "   ";

        return str;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
