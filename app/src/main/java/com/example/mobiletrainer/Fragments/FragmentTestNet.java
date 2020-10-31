package com.example.mobiletrainer.Fragments;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.mobiletrainer.CSV;
import com.example.mobiletrainer.Classifier;
import com.example.mobiletrainer.Constants;
import com.example.mobiletrainer.DataFormat;
import com.example.mobiletrainer.Mathematics;
import com.example.mobiletrainer.R;
import com.example.mobiletrainer.Utils;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static com.example.mobiletrainer.Constants.NETWORK_FILE;


public class FragmentTestNet extends FragmentSensorSmart implements SeekBar.OnSeekBarChangeListener {

    /**Фрагмент с определением физической активности.
     * Данные с даттчиков поступают на нейросеть и результат отражается в TextView
     */

    Classifier classifier;


    String strings= " ";
    TextView tv_log;
    Switch sw_isTraining;
    SeekBar sb_accuracy;


    public FragmentTestNet() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);

        View parent = inflater.inflate(R.layout.fragment_test_net, container, false);
        tv_log = parent.findViewById(R.id.tv_log);
        sb_accuracy = parent.findViewById(R.id.sb_accuracy);
        sb_accuracy.setOnSeekBarChangeListener(this);

        sw_isTraining = parent.findViewById(R.id.sw_isTraining);

        sw_isTraining.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               setTvText((b ? "Начинаем тренировку!" : "Тренировка окончена"));
            }
        });


        classifier = new Classifier(Utils.assetFilePath(getContext(),NETWORK_FILE));


        setTvText("Привет, я буду считать твои упражнения");

        return parent;
    }

    public void setTvText(String text){
        strings += text + "\n";
        tv_log.setText(strings);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        super.onSensorChanged(sensorEvent);
        if(sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            if(sw_isTraining.isChecked()){
                if(!isWriting && (findRotationGrow() || findGrow())){
                    isWriting = true;
                    meow();
                }

                if(isWriting && findRotationRow() && findRow()){
                    isWriting = false;
                    kok();

                    float[] linear = Mathematics.processData(linearList,N0);
                    float[] gravity = Mathematics.processData(gravityList,N0);
                    float[] gyroscope = Mathematics.processData(gyroList,N0);

                    float[] data = ArrayUtils.addAll(ArrayUtils.addAll(linear,gravity),gyroscope);

                    int result = classifier.predict(data);

                    updateUIwithResult(result);
                    clearArrays();

                }

            }
        }
    }

    public void updateUIwithResult(int result){
        switch (result){
            case -1: {
                setTvText("Упражнение не распознано" +" ( " + DataFormat.format(Classifier.current_probability)  +" ) " );
                break;
            }

            case Constants.HAND_ROTATION:{
                setTvText("Вы сделали вращение руками"+" ( " + DataFormat.format(Classifier.current_probability)  +" ) " );
                break;
            }
            case Constants.LUNGE:{
                setTvText("Вы сделали выпад на ногу"+" ( " + DataFormat.format(Classifier.current_probability)  +" ) " );
                break;
            }

            case Constants.PUSH_UPS:{
                setTvText("Вы сделали отжимание"+" ( " + DataFormat.format(Classifier.current_probability)  +" ) " );
                break;
            }
            case Constants.HAND_LIFTS:{
                setTvText("Вы сделали подъем рук"+" ( " + DataFormat.format(Classifier.current_probability)  +" ) " );
                break;
            }
            case  Constants.PRESS:{
                setTvText("Вы сделали упражнение на пресс"+" ( " + DataFormat.format(Classifier.current_probability)  +" ) " );
                break;
            }

            case Constants.SQUATTING:{
                setTvText("Вы сделали приседание"+" ( " + DataFormat.format(Classifier.current_probability)  +" ) " );
                break;
            }

            case Constants.JUMPING:{
                setTvText("Вы сделали прыжок"+" ( " + DataFormat.format(Classifier.current_probability)  +" ) " );
                break;
            }


        }
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Classifier.PROBABILITY_THRESHOLD = 0.80 + progress*0.03;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}