package com.example.mobiletrainer.Fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class FragmentSensor extends Fragment implements SensorEventListener {

    /** Класс реализующий работу датчиков для фрагментов, которые наследуются от этого класса
     * Тут считываются все данные с датчиков
     * */

    SensorManager manager;
    // Датчики
    Sensor accelerometer, gyroscope, gravity, rotation, linear;

    // Наличие датчиков на устройстве
    boolean isAccel = true, isGyro = true, isGravity = true, isLinear = true, isRotation = true;

    // Сюда записываются данные, когда они приходят с датчиков
    double[] gravityComponents = new double[3];
    double[] accelerometerComponents = new double[3];
    double[] gyroscopeComponents = new double[3];
    double[] rotationComponents = new double[3];
    double[] linearComponents = new double[3];

    // Данные матрицы вращения
    // TODO: Это экспериментальные переменные. Определиться нужны или нет.
    private final float[] rotationMatrix = new float[16];
    private final float[] prevRotationMatrix = new float[16];
    private final float[] angleShiftBetweenMatrix = new float[3];
    double[] angleShift = new double[3];



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /**Регистрация всех датчиков*/
        manager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);

        if (manager!=null){
            /**Проверяем наличие датчиков*/
            accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer==null){
                isAccel = false;
            }

            gyroscope = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            if(gyroscope == null){
                isGyro = false;
            }

            gravity = manager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            if(gravity == null){
                isGravity = false;
            }

            rotation = manager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);

            /**Для определённости необходимо задавать начальное состояние матрицы*/
            rotationMatrix[0] = prevRotationMatrix[0] = 1;
            rotationMatrix[4] = prevRotationMatrix[4] = 1;
            rotationMatrix[8] = prevRotationMatrix[8] = 1;
            rotationMatrix[12] = prevRotationMatrix[12] = 1;

            if(rotation == null){
                isRotation = false;
            }

            linear = manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            if(linear==null){
                isLinear= false;
            }

        }
        // для наследников этого класса
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        /**Слушаем датчики*/
        super.onResume();

        if (isAccel) {
            manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        }

        if (isGyro) {
            manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
        }

        if (isGravity) {
            manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_FASTEST);
        }

        if (isRotation) {
            manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR), 20);
        }

        if (isLinear) {
            manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        /**Все измеренные компоненты датчиков обновляются и хранятся в соответствующих переменных
         * к которым имеют доступ классы, наслодовавшиеся от этого класса*/

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerComponents = new double[3];
            for (int i = 0; i < 3; i++) {
                accelerometerComponents[i] = sensorEvent.values[i];
            }
        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroscopeComponents = new double[3];
            for (int i = 0; i < 3; i++) {
                gyroscopeComponents[i] = sensorEvent.values[i];
            }
        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_GRAVITY) {
            gravityComponents = new double[3];
            for (int i = 0; i < 3; i++) {
                gravityComponents[i] = sensorEvent.values[i];
            }
        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            rotationComponents = new double[3];
            for (int i = 0; i < 3; i++) {
                rotationComponents[i] = sensorEvent.values[i];
            }

            /**C помощью изменения матрица вращения измеряется скорость изменения угла
             * Оказалось, что этот датчик даёт не самые лучшие результаты, над ним ведется работа
             * */

            SensorManager.getRotationMatrixFromVector(rotationMatrix,sensorEvent.values);
            SensorManager.getAngleChange(angleShiftBetweenMatrix, rotationMatrix,prevRotationMatrix);

            angleShift = new double[3];
            for(int i =0; i<3;i++){
                angleShift[i] = (double) angleShiftBetweenMatrix[i] * 10;
            }

            System.arraycopy(rotationMatrix,0,prevRotationMatrix,0,16);


        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            linearComponents = new double[3];
            for (int i = 0; i < 3; i++) {
                linearComponents[i] = sensorEvent.values[i];
            }
        }
    }


    @Override
    public void onPause(){
        /**Перестаем слушать датчики*/
        super.onPause();

        if(isAccel && isGyro && isGravity){
            manager.unregisterListener(this);
        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

}
