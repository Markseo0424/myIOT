package com.example.room_iot.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.example.room_iot.supports.Communicate;
import com.example.room_iot.supports.Memories;
import com.example.room_iot.R;
import com.example.room_iot.databinding.FragmentNotificationsBinding;
import com.example.room_iot.types.AppResponse;
import com.example.room_iot.caller.AppResponseListener;

import org.json.JSONException;

public class NotificationsFragment extends Fragment {
    private Memories memories;
    private FragmentNotificationsBinding binding;
    private Communicate communicate;
    boolean isButtonValid = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        memories = new Memories(getActivity());
        communicate = new Communicate(getActivity());

        View root = binding.getRoot();
        binding.changeUrlButton.setBackgroundTintList(getResources().getColorStateList(R.color.button_set_2,null));

        binding.checkConnectionButton.setOnClickListener(view -> {
            try {
                communicate.checkConnection(binding.newURL.getText().toString(), new AppResponseListener() {
                    @Override
                    public void onResponse(AppResponse appResponse) throws JSONException {
                        if(appResponse.isOK()) {
                            Toast.makeText(getActivity(), "Connection Valid", Toast.LENGTH_SHORT).show();
                            isButtonValid = true;
                            binding.changeUrlButton.setEnabled(true);
                            binding.newURL.setEnabled(false);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Toast.makeText(getActivity(), "Connection Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        binding.changeUrlButton.setOnClickListener(view -> {
            Communicate.url = binding.newURL.getText().toString();
            memories.setData("url", binding.newURL.getText().toString());
            Toast.makeText(getActivity(), "URL Changed", Toast.LENGTH_SHORT).show();
            onStart();
        });

        return root;
    }

    @Override
    public void onStart() {
        isButtonValid = false;
        binding.currentURL.setText(Communicate.url);
        binding.newURL.setText(Communicate.url);
        memories.setData("url",Communicate.url);
        binding.newURL.setEnabled(true);
        binding.changeUrlButton.setEnabled(false);
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}