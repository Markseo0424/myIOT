package com.example.room_iot.ui.modules;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.room_iot.supports.AlertPrint;
import com.example.room_iot.supports.Communicate;
import com.example.room_iot.supports.Memories;
import com.example.room_iot.caller.AppCaller;
import com.example.room_iot.databinding.FragmentModulesBinding;
import com.example.room_iot.types.AppRequest;
import com.example.room_iot.types.AppResponse;
import com.example.room_iot.caller.AppResponseListener;
import com.example.room_iot.types.Module;

import org.json.JSONArray;
import org.json.JSONException;

public class ModulesFragment extends Fragment {
    private Memories memories;
    private FragmentModulesBinding binding;

    Module[] modules;

    public void Sync() throws JSONException {
        Communicate communicate = new Communicate(getActivity());
        communicate.sendRequest(new AppRequest(), new AppResponseListener() {
            @Override
            public void onResponse(AppResponse appResponse) throws JSONException {
                Module[] moduleList = memories.getModules();
                for(int i = 0; i < moduleList.length; i++) {
                    String id = moduleList[i].getModuleId();
                    memories.editVal(id, appResponse.getVal(id));
                }
                AppCaller.callSyncListener();
            }
        });
    }

    public ModulesFragment() throws JSONException {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentModulesBinding.inflate(inflater, container, false);
        memories = new Memories(getActivity());
        View root = binding.getRoot();

        binding.fab.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(),ModuleActivity.class);
            startActivity(intent);
        });


        binding.syncButton.setOnClickListener(view -> {
            try {
                (new Communicate(getContext())).sendRequest(new AppRequest(), new AppResponseListener() {
                    @Override
                    public void onResponse(AppResponse appResponse) throws JSONException {
                        AlertPrint.print(getContext(),appResponse.getListDat().toString(),"HELLO");
                        Module[] moduleList = memories.getModules();
                        for (Module module : moduleList) {
                            String id = module.getModuleId();
                            memories.editVal(id, appResponse.getVal(id));
                        }
                        AppCaller.callSyncListener();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        binding.button2.setOnClickListener(view -> {
            JSONArray modules = null;
            try {
                modules = memories.getObjectsJSON(Memories.MODULES);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            AlertPrint.print(getContext(),modules.toString(),"HI");
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        /*
        try {
            Sync();
        } catch (JSONException e) {
            e.printStackTrace();
        }

         */

        try {
            modules = memories.getModules();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListAdapter listAdapter = new ModuleAdapter(getActivity(), modules);
        binding.moduleList.setAdapter(listAdapter);

        binding.moduleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(),EditModuleActivity.class);
                intent.putExtra("moduleId",modules[i].getModuleId());
                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}