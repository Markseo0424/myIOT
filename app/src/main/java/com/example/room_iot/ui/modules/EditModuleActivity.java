package com.example.room_iot.ui.modules;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.room_iot.supports.Communicate;
import com.example.room_iot.supports.Memories;
import com.example.room_iot.databinding.ActivityEditModuleBinding;
import com.example.room_iot.types.AppRequest;
import com.example.room_iot.types.AppResponse;
import com.example.room_iot.caller.AppResponseListener;

import org.json.JSONException;

public class EditModuleActivity extends AppCompatActivity {
    Memories memories;
    ActivityEditModuleBinding binding;
    Communicate communicate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        memories = new Memories(this);
        communicate = new Communicate(this);
        binding = ActivityEditModuleBinding.inflate(getLayoutInflater());

        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        String moduleId = intent.getStringExtra("moduleId");
        String moduleName = null;

        try {
            moduleName = memories.findModuleById(moduleId).getModuleName();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        binding.idText2.setHint(moduleId);
        binding.nameText2.setText(moduleName);

        binding.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    memories.editName(moduleId, binding.nameText2.getText().toString());
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        binding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditModuleActivity.this);
                builder.setMessage("Really wanna delete module?");
                builder.setTitle("Delete alert")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    memories.delModule(moduleId);
                                    Toast.makeText(EditModuleActivity.this, "Deleted module", Toast.LENGTH_SHORT).show();
                                    try {
                                        communicate.sendRequest(new AppRequest(moduleId,AppRequest.DEL),new AppResponseListener() {
                                            @Override
                                            public void onResponse(AppResponse appResponse) throws JSONException {
                                                if(appResponse.isOK()) Toast.makeText(EditModuleActivity.this, "Removed from server", Toast.LENGTH_SHORT).show();
                                                else Toast.makeText(EditModuleActivity.this, "Failed to remove from server", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
}