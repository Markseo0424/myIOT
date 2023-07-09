package com.example.room_iot.ui.schedules;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.room_iot.supports.Memories;
import com.example.room_iot.R;
import com.example.room_iot.databinding.ActivityScheduleBinding;
import com.example.room_iot.types.AppAction;
import com.example.room_iot.types.AppCondition;
import com.example.room_iot.types.Schedule;
import com.example.room_iot.ui.schedules.action.ActionActivity;
import com.example.room_iot.ui.schedules.action.adapter.ActionAdapter;
import com.example.room_iot.ui.schedules.action.callback.ActionItemTouchCallback;
import com.example.room_iot.ui.schedules.action.listener.OnActionStartDragListener;
import com.example.room_iot.ui.schedules.action.viewholder.ActionModuleViewholder;
import com.example.room_iot.ui.schedules.condition.ConditionActivity;
import com.example.room_iot.ui.schedules.condition.adapter.ConditionAdapter;
import com.example.room_iot.ui.schedules.condition.callback.ConditionItemTouchCallback;
import com.example.room_iot.ui.schedules.condition.listener.OnConditionStartDragListener;
import com.example.room_iot.ui.schedules.condition.viewholder.ConditionModuleViewholder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity
implements OnConditionStartDragListener, OnActionStartDragListener {
    ActivityScheduleBinding binding;

    ConditionAdapter conditionAdapter;
    RecyclerView conditionRecyclerView;
    LinearLayoutManager conditionLinearLayoutManager;
    ItemTouchHelper conditionItemTouchHelper;

    ActionAdapter actionAdapter;
    RecyclerView actionRecyclerView;
    LinearLayoutManager actionLinearLayoutManager;
    ItemTouchHelper actionItemTouchHelper;

    List<AppCondition> appConditions = new ArrayList<>();
    List<AppAction> appActions = new ArrayList<>();

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            Intent intent = result.getData();
            if(result.getResultCode() == 301) {
                try {
                    conditionAdapter.addItem(new AppCondition(new JSONObject(intent.getStringExtra("condition"))));
                } catch (JSONException e) {e.printStackTrace();}
            }
            else if(result.getResultCode() == 302) {
                try {
                    actionAdapter.addItem(new AppAction(new JSONObject(intent.getStringExtra("action"))));
                } catch (JSONException e) {e.printStackTrace();}
            }
            binding.addScheduleButton.setEnabled(conditionAdapter.getItemCount() > 0 && actionAdapter.getItemCount() > 0);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScheduleBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());



        binding.addConditionButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, ConditionActivity.class);
            launcher.launch(intent);
        });

        binding.addActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, ActionActivity.class);
            launcher.launch(intent);
        });

        Intent intent = getIntent();
        if(intent.getStringExtra("mode").equals("add")) {
            binding.addScheduleButton.setOnClickListener(view -> {
                Memories memories = new Memories(ScheduleActivity.this);
                appConditions = conditionAdapter.getItems();
                appActions = actionAdapter.getItems();
                String name = binding.nameText4.getText().toString();
                try {
                    if (memories.addSchedule(new Schedule(name, appConditions, appActions))) {
                        Toast.makeText(ScheduleActivity.this, "Schedule added.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else
                        Toast.makeText(ScheduleActivity.this, "Same name! try again.", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
        else {
            Schedule schedule = new Schedule("NULL", appConditions,appActions);
            Memories memories = new Memories(ScheduleActivity.this);
            try {
                schedule = (new Memories(this)).getSchedule(intent.getStringExtra("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Schedule finalSchedule = schedule;

            appConditions = schedule.getConditions();
            appActions = schedule.getActions();
            binding.nameText4.setText(schedule.getName());

            binding.addScheduleButton.setText("edit schedule");
            binding.deleteScheduleButton.setVisibility(View.VISIBLE);
            binding.addScheduleButton.setEnabled(true);
            binding.addScheduleButton.setOnClickListener(view -> {
                appConditions = conditionAdapter.getItems();
                appActions = actionAdapter.getItems();

                for(int i = 0; i < appConditions.size(); i++) {
                    appConditions.get(i).isOn = true;
                }

                int index = -1;
                try {
                    index = memories.findSchedule(finalSchedule.getName());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String name = binding.nameText4.getText().toString();

                try {
                    if (memories.editSchedule(new Schedule(name, appConditions, appActions), index)) {
                        Toast.makeText(ScheduleActivity.this, "Schedule edited.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else
                        Toast.makeText(ScheduleActivity.this, "Same name! try again.", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
            binding.deleteScheduleButton.setOnClickListener(view -> {
                try {
                    memories.removeSchedule(finalSchedule.getName());
                    Toast.makeText(ScheduleActivity.this, "Schedule removed.", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finish();
            });

        }


        //Condition
        conditionAdapter = new ConditionAdapter(this,this);
        conditionRecyclerView = findViewById(R.id.conditionView);
        conditionLinearLayoutManager = new LinearLayoutManager(this);

        conditionAdapter.setItems(appConditions);

        conditionRecyclerView.setLayoutManager(conditionLinearLayoutManager);
        conditionRecyclerView.setAdapter(conditionAdapter);

        ConditionItemTouchCallback conditionCallback = new ConditionItemTouchCallback(conditionAdapter);
        conditionItemTouchHelper = new ItemTouchHelper(conditionCallback);
        conditionItemTouchHelper.attachToRecyclerView(conditionRecyclerView);

        //Action
        actionAdapter = new ActionAdapter(this,this);
        actionRecyclerView = findViewById(R.id.actionView);
        actionLinearLayoutManager = new LinearLayoutManager(this);

        actionAdapter.setItems(appActions);

        actionRecyclerView.setLayoutManager(actionLinearLayoutManager);
        actionRecyclerView.setAdapter(actionAdapter);

        ActionItemTouchCallback actionCallback = new ActionItemTouchCallback(actionAdapter);
        actionItemTouchHelper = new ItemTouchHelper(actionCallback);
        actionItemTouchHelper.attachToRecyclerView(actionRecyclerView);
    }

    @Override
    public void onStartDrag(ConditionModuleViewholder viewHolder) {
        conditionItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onStartDrag(ActionModuleViewholder viewHolder) {
        actionItemTouchHelper.startDrag(viewHolder);
    }
}