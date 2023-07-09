package com.example.room_iot.ui.schedules.action.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room_iot.supports.Memories;
import com.example.room_iot.R;
import com.example.room_iot.types.AppAction;
import com.example.room_iot.ui.schedules.action.listener.OnActionItemMoveListener;
import com.example.room_iot.ui.schedules.action.listener.OnActionStartDragListener;
import com.example.room_iot.ui.schedules.action.viewholder.ActionModuleViewholder;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActionAdapter extends RecyclerView.Adapter<ActionModuleViewholder>
implements OnActionItemMoveListener, ActionModuleViewholder.deleteButtonListener, ActionModuleViewholder.editDelayListener {
    OnActionStartDragListener dragListener;
    List<AppAction> items = new ArrayList<>();
    Context context;

    public ActionAdapter(Context context, OnActionStartDragListener dragListener) {
        this.dragListener = dragListener;
        this.context = context;
    }

    public void setItems(List<AppAction> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addItem(AppAction item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public List<AppAction> getItems() {
        return items;
    }

    @NonNull
    @Override
    public ActionModuleViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_action,parent,false);
        ActionModuleViewholder holder = new ActionModuleViewholder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ActionModuleViewholder holder, int position) {
        AppAction item = items.get(position);
        holder.setListeners(this,this);

        try {
            holder.title.setText((new Memories(context).findModuleById(item.getModuleId()).getModuleName()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.text.setText(item.getText());

        holder.moveHandle.setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                dragListener.onStartDrag(holder);
            }
            return false;
        });

        holder.delayTimeText.setText(String.valueOf(item.getDelayTime()));

    }

    public void updateDelay() {
        for(int i = 0; i < items.size(); i++) {

        }
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
    public void onChange(int position, int delay) {
        items.get(position).setDelayTime(delay);
        Toast.makeText(context, "Delay time set", Toast.LENGTH_SHORT).show();
    }

}
