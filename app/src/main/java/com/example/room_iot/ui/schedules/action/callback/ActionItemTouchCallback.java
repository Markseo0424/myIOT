package com.example.room_iot.ui.schedules.action.callback;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room_iot.ui.schedules.action.listener.OnActionItemMoveListener;
import com.example.room_iot.ui.schedules.condition.listener.OnConditionItemMoveListener;

public class ActionItemTouchCallback extends ItemTouchHelper.Callback {
    OnActionItemMoveListener moveListener;

    public ActionItemTouchCallback(OnActionItemMoveListener moveListener) {
        this.moveListener = moveListener;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        moveListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }
}
