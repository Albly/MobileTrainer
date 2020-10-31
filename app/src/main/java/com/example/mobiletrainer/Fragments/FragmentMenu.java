package com.example.mobiletrainer.Fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mobiletrainer.R;

public final class FragmentMenu extends Fragment implements View.OnClickListener {

    /** Фрагмент меню.
     * Переключает с помощью кнопок другие окна приложения
     * */

    Button btn_testNet, btn_writeData, btn_sensors;
    FragmentCallback callback;


    public FragmentMenu() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        if(activity instanceof FragmentCallback){
            callback = (FragmentCallback)activity;
        }else {
            throw new RuntimeException((activity.toString()+" must implement FragmentCallback"));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View parent = inflater.inflate(R.layout.fragment_menu, container, false);
        btn_testNet = parent.findViewById(R.id.btn_net);
        btn_writeData = parent.findViewById(R.id.btn_dataset);
        btn_sensors = parent.findViewById(R.id.btn_sensors);


        btn_testNet.setOnClickListener(this);
        btn_writeData.setOnClickListener(this);
        btn_sensors.setOnClickListener(this);

        return parent;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_dataset:{
                callback.getPressedButton(FragmentCallback.BTN_WRITE_DATASET);
                break;
            }
            case R.id.btn_net:{
                callback.getPressedButton(FragmentCallback.BTN_TEST);
                break;
            }

            case  R.id.btn_sensors:{
                callback.getPressedButton(FragmentCallback.BTN_SENSORS);
                break;
            }
        }
    }
}