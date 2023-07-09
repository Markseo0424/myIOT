package com.example.room_iot.ui.schedules.condition.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room_iot.supports.Memories;
import com.example.room_iot.R;
import com.example.room_iot.types.AppCondition;
import com.example.room_iot.ui.schedules.condition.listener.OnConditionItemMoveListener;
import com.example.room_iot.ui.schedules.condition.listener.OnConditionStartDragListener;
import com.example.room_iot.ui.schedules.condition.viewholder.ConditionModuleViewholder;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConditionAdapter extends RecyclerView.Adapter<ConditionModuleViewholder>
implements OnConditionItemMoveListener, ConditionModuleViewholder.deleteButtonListener, ConditionModuleViewholder.blendSwitchListener {
    OnConditionStartDragListener dragListener;
    List<AppCondition> items = new ArrayList<>();
    Context context;

    public ConditionAdapter(Context context, OnConditionStartDragListener dragListener) {
        this.dragListener = dragListener;
        this.context = context;
    }

    public void setItems(List<AppCondition> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addItem(AppCondition item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public List<AppCondition> getItems() {
        return items;
    }

    @NonNull
    @Override
    public ConditionModuleViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_condition,parent,false);
        ConditionModuleViewholder holder = new ConditionModuleViewholder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ConditionModuleViewholder holder, int position) {
        AppCondition item = items.get(position);
        String title = "";
        if (item.getType().equals(AppCondition.TIME)) title = "Time";
        else {
            try {
                title = (new Memories(context)).findModuleById(item.getModuleId()).getModuleName();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        holder.setListeners(this,this);
        holder.title.setText(title);
        holder.text.setText(item.getText());
        holder.blendSwitch.setChecked(item.getBlend() == AppCondition.AND);
        holder.moveHandle.setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                dragListener.onStartDrag(holder);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(items, fromPosition, toPosition);
        notifyItemMoved(fromPosition,toPosition);
        return false;
    }

    @Override
    public void onClick(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onTab(int position, boolean b) {
        items.get(position).setBlend(b? AppCondition.AND : AppCondition.OR);
    }
}
