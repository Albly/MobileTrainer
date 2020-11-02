package com.example.mobiletrainer.Fragments;

import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.mobiletrainer.DataFormat;
import com.example.mobiletrainer.R;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.mobiletrainer.DataFormat.prepareData;


public final class FragmentTestSensors extends FragmentSensor {

    /**Фрагмент, просто отображающий данные датчиков*/

    CheckBox cb_Accel, cb_Gyro, cb_Gravity, cb_Rotation, cb_Linear;
    TextView tv_Accel, tv_Gyro, tv_Gravity, tv_Rotation, tv_Linear, tv_AbsRot;

    private Timer timer;


    public FragmentTestSensors() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
        super.onResume();

        //Обновляем UI с с периодом 200 мс
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       updateUI();
                    }
                });
            }
        };
        timer.schedule(task, 0, 200);

    }

    @Override
    public void onPause(){
        timer.cancel();
        super.onPause();
    }

    private void updateUI(){
        /**Обновляем UI*/
        tv_Accel.setText(prepareData(accelerometerComponents));
        tv_Gyro.setText(prepareData(gyroscopeComponents));
        tv_Gravity.setText(prepareData(gravityComponents));
        tv_Rotation.setText(prepareData(rotationComponents));
        tv_Linear.setText(prepareData(linearComponents));
        tv_AbsRot.setText(prepareData(angleShift));


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.fragment_test_sensors, container, false);

        //инициализация UI компонентов
        cb_Accel = parent.findViewById(R.id.cb_accel);
        cb_Gravity = parent.findViewById(R.id.cb_gravity);
        cb_Gyro = parent.findViewById(R.id.cb_gyro);
        cb_Rotation = parent.findViewById(R.id.cb_rotation);
        cb_Linear = parent.findViewById(R.id.cb_linear);

        tv_Accel = parent.findViewById(R.id.tv_accel);
        tv_Gyro = parent.findViewById(R.id.tv_gyro);
        tv_Gravity = parent.findViewById(R.id.tv_gravity);
        tv_Rotation = parent.findViewById(R.id.tv_rotation);
        tv_Linear = parent.findViewById(R.id.tv_linear);
        tv_AbsRot = parent.findViewById(R.id.tv_absRot);

        super.onCreateView(inflater,container,savedInstanceState);
        return parent;
    }
}