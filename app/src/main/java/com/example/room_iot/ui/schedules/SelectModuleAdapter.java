package com.example.room_iot.ui.schedules;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.room_iot.R;
import com.example.room_iot.types.Module;

public class SelectModuleAdapter extends ArrayAdapter<Module> {

    public SelectModuleAdapter(@NonNull Context context, @NonNull Module[] objects) {
        super(context, R.layout.adapter_select_module, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Module item = getItem(position);
        LayoutInflater imageInflater = LayoutInflater.from(getContext());
        View view = imageInflater.inflate(R.layout.adapter_select_module, parent,false);

        TextView name = view.findViewById(R.id.moduleName);
        TextView id = view.findViewById(R.id.moduleId);
        TextView type = view.findViewById(R.id.moduleType);

        name.setText(item.getModuleName());
        id.setText(item.getModuleId());
        type.setText(item.getType());

        return view;
    }
}
