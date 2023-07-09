package com.example.room_iot.ui.modules;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.room_iot.supports.Communicate;
import com.example.room_iot.supports.Memories;
import com.example.room_iot.databinding.ActivityModuleBinding;
import com.example.room_iot.types.AppRequest;
import com.example.room_iot.types.AppResponse;
import com.example.room_iot.caller.AppResponseListener;
import com.example.room_iot.types.Module;

import org.json.JSONException;

public class ModuleActivity extends AppCompatActivity {
    ActivityModuleBinding binding;
    Memories memories;
    Communicate communicate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        memories = new Memories(this);
        communicate = new Communicate(this);
        binding = ActivityModuleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = binding.idText.getText().toString();
                String name = binding.nameText.getText().toString();

                AppRequest appRequest = new AppRequest(id, AppRequest.NEW);
                try {
                    communicate.sendRequest(appRequest, new AppResponseListener() {
                        @Override
                        public void onResponse(AppResponse appResponse) throws JSONException {
                            if(!appResponse.isOK()) return;
                            Module module = appResponse.getModule();
                            module.setModuleName(name);
                            int state = -1;
                            try {
                                state = memories.addModule(module);
                            } catch (JSONException e) {
                                Toast.makeText(ModuleActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                            if(state == 0) {
                                Toast.makeText(ModuleActivity.this, "Module successfully added", Toast.LENGTH_SHORT).show();
                                ModuleActivity.this.finish();
                            }
                            else if(state == 1) Toast.makeText(ModuleActivity.this, "Same ID! Module add denied", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(VolleyError error) {
                            Toast.makeText(ModuleActivity.this, "no ID. try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}