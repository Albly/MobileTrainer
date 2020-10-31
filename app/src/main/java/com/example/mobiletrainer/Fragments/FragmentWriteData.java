package com.example.mobiletrainer.Fragments;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.example.mobiletrainer.CSV;
import com.example.mobiletrainer.R;

import static com.example.mobiletrainer.Mathematics.analyze;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentWriteData#newInstance} factory method to
 * create an instance of this fragment.
 */
public final class FragmentWriteData extends FragmentSensorSmart {


    /**
     * Фрагмент для записи датасета. Наследуется от класса, реализующий датчики и
     * поиск движения по ним
     * */

    Switch sw_start, sw_lock;
    RadioGroup rg_group;
    TextView tv_count;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentWriteData() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentWriteData.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentWriteData newInstance(String param1, String param2) {
        FragmentWriteData fragment = new FragmentWriteData();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);

        View parent = inflater.inflate(R.layout.fragment_write_data, container, false);
        sw_lock = parent.findViewById(R.id.sv_lock);
        sw_start = parent.findViewById(R.id.sw_start);

        tv_count = parent.findViewById(R.id.tv_count);
        tv_count.setText(String.valueOf(CSV.getFileCount()));
        rg_group = parent.findViewById(R.id.rg_group);

        sw_lock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                for(int i =0; i < rg_group.getChildCount(); i++){
                    rg_group.getChildAt(i).setEnabled(!b);
                }
            }
        });

        return parent;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        super.onSensorChanged(sensorEvent);

        //TODO Переместить в обработку нажатия кнопки
        if(sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            if(sw_start.isChecked()){
                if(!isWriting && (findRotationGrow() || findGrow())){
                    isWriting = true;
                    meow();
                }

                if(isWriting && findRotationRow() && findRow()){
                    isWriting = false;
                    kok();
                    listToCSV();
                    clearArrays();
                    CSV.increment_count();
                    updateUI();
                }
            }
        }

    }

    void updateUI(){
        tv_count.setText(String.valueOf(CSV.getFileCount()));
    }

    public int getSelectedExercise(){
        int radioButtonID = rg_group.getCheckedRadioButtonId();
        View radioButton = rg_group.findViewById(radioButtonID);
        int idx = rg_group.indexOfChild(radioButton);
        return idx;
    }

    public void listToCSV(){

        int selectedExercise = getSelectedExercise();
        analyze(accelList, N0, Sensor.TYPE_ACCELEROMETER, selectedExercise);
        analyze(rotationList, N0, Sensor.TYPE_GAME_ROTATION_VECTOR,selectedExercise);
        analyze(gravityList, N0, Sensor.TYPE_GRAVITY,selectedExercise);
        analyze(gyroList, N0, Sensor.TYPE_GYROSCOPE,selectedExercise);
        analyze(linearList, N0 ,Sensor.TYPE_LINEAR_ACCELERATION,selectedExercise);
        analyze(angleShiftList, N0, -10001, selectedExercise);

    }

    public void kok(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.kok);
                mp.start();
            }
        }).start();
    }

    public void meow(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.kek);
                mp.start();
            }
        }).start();
    }

}