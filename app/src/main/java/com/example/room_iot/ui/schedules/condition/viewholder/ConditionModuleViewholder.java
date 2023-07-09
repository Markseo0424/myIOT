package com.example.room_iot.ui.schedules.condition.viewholder;

import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room_iot.R;

public class ConditionModuleViewholder extends RecyclerView.ViewHolder {
    public TextView title, text;
    public Button deleteButton;
    public ImageButton moveHandle;
    public ToggleButton blendSwitch;
    private deleteButtonListener deleteButtonListener;
    private blendSwitchListener blendSwitchListener;

    public void setListeners(deleteButtonListener listener1, blendSwitchListener listener2) {
        deleteButtonListener = listener1;
        blendSwitchListener = listener2;
    }

    public ConditionModuleViewholder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.conditionTitle);
        text = itemView.findViewById(R.id.conditionText);
        deleteButton = itemView.findViewById(R.id.deleteConditionButton);
        moveHandle = itemView.findViewById(R.id.moveConditionButton);
        blendSwitch = itemView.findViewById(R.id.changeBlendButton);

        deleteButton.setOnClickListener(view -> {
            deleteButtonListener.onClick(getAdapterPosition());
        });

        blendSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            blendSwitchListener.onTab(getAdapterPosition(), b);
        });
    }

    public interface deleteButtonListener {
        void onClick(int position);
    }
    public interface blendSwitchListener {
        void onTab(int position, boolean b);
    }
}
