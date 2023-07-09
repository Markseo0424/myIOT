package com.example.room_iot.ui.schedules.action.viewholder;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room_iot.R;

public class ActionModuleViewholder extends RecyclerView.ViewHolder {
    public TextView title, text;
    public Button deleteButton;
    public ImageButton moveHandle;
    private deleteButtonListener buttonListener;
    private editDelayListener delayListener;
    public EditText delayTimeText;

    public void setListeners(deleteButtonListener listener1, editDelayListener listener2) {
        buttonListener = listener1;
        delayListener = listener2;
    }

    public ActionModuleViewholder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.actionModuleName);
        text = itemView.findViewById(R.id.actionText);
        deleteButton = itemView.findViewById(R.id.deleteActionButton);
        moveHandle = itemView.findViewById(R.id.moveActionButton);
        delayTimeText = itemView.findViewById(R.id.delaySecondsText);

        deleteButton.setOnClickListener(view -> {
            buttonListener.onClick(getAdapterPosition());
        });

        delayTimeText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                delayListener.onChange(getAdapterPosition(), Integer.parseInt(delayTimeText.getText().toString()));
                return false;
            }
        });
    }

    public interface deleteButtonListener {
        void onClick(int position);
    }

    public interface editDelayListener {
        void onChange(int position, int delay);
    }
}
